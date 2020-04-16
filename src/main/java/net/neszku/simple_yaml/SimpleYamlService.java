package net.neszku.simple_yaml;

import net.neszku.simple_yaml.adapter.TypeAdapter;
import net.neszku.simple_yaml.steorotype.Refers;
import net.neszku.simple_yaml.helpers.LookupHelper;
import net.neszku.simple_yaml.helpers.ProxyHelper;
import net.neszku.simple_yaml.helpers.reflections.ReflectionHelper;
import net.neszku.simple_yaml.naming.NamingStrategy;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SimpleYamlService {

    private final Yaml yaml;
    private final List<TypeAdapter<?>> adapters;

    public SimpleYamlService(SimpleYamlBuilder builder) {
        this.adapters = builder.adapters;
        this.yaml = new Yaml(
            builder.representer,
            builder.dumper
        );
    }

    public List<TypeAdapter<?>> getTypeAdapters() {
        return adapters;
    }

    public TypeAdapter<?> getTypeAdapterFor(Class<?> clazz) {
        return adapters.stream()
                .filter(adapter -> adapter.getType().isAssignableFrom(clazz))
                .findAny()
                .orElse(null);
    }


    public String dump(SimpleYamlData data) {
        return yaml.dump(data.dataMap);
    }

    public String dump(Class<?> clazz) {
        NamingStrategy strategy = ReflectionHelper.getStrategy(clazz);
        Map<String, Object> defaultValues = getDefaultValues(clazz, null, this, strategy);
        return yaml.dump(defaultValues);
    }

    public String dump(Class<?> clazz, Object instance) {
        NamingStrategy strategy = ReflectionHelper.getStrategy(clazz);
        Map<String, Object> defaultValues = getDefaultValues(clazz, instance, this, strategy);
        return yaml.dump(defaultValues);
    }

    public <T> T load(Class<T> clazz, String yamlString) {
        NamingStrategy strategy = ReflectionHelper.getStrategy(clazz);
        SimpleYamlData data = new SimpleYamlData(yaml.load(yamlString));

        adapt(clazz, "", strategy, data);

        Map<String, String> setters = mapSetters(clazz, new LinkedHashMap<>(), "", strategy);
        return ProxyHelper.proxyingWithSubInterfaces(clazz, new ProxyTrafficHandler(data, clazz, strategy, setters));
    }

    private <T> void adapt(Class<T> clazz, String path, NamingStrategy strategy, SimpleYamlData data) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isDefault()) {
                continue;
            }

            try {
                String name = strategy.apply(method.getName());
                Object value = handle(data.get(path + name), method, this, Operation.DESERIALIZE);
                data.set(path + name, value);
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        for (Class<?> nested : clazz.getDeclaredClasses()) {
            String name = strategy.apply(nested.getSimpleName());
            adapt(nested, path + name + ".", strategy, data);
        }
    }

    private Map<String, Object> getDefaultValues(Class<?> clazz, Object instance, SimpleYamlService yaml, NamingStrategy strategy) {

        Map<String, Object> values = new LinkedHashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isDefault()) {
                continue;
            }

            try {
                String name = strategy.apply(method.getName());
                Object value = handle(LookupHelper.lookupValue(clazz, method, instance), method, yaml, Operation.SERIALIZE);
                values.put(name, value);
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        for (Class<?> nested : clazz.getDeclaredClasses()) {
            String name = strategy.apply(nested.getSimpleName());
            values.put(name, getDefaultValues(nested, instance, yaml, strategy));
        }

        return values;
    }

    private Object handle(Object object, Method method, SimpleYamlService yaml, Operation operation) {
        List<Class<?>> generics = ReflectionHelper.getGenericReturnType(method);
        Class<?> clazz = method.getReturnType();

        if (Collection.class.isAssignableFrom(clazz)) {
            return handleCollection(cast(object), generics.get(0), yaml, operation);
        }

        if (Map.class.isAssignableFrom(clazz)) {
            return handleMap(cast(object), generics.get(0), generics.get(1), yaml, operation);
        }

        TypeAdapter<?> adapter = yaml.getTypeAdapterFor(clazz);
        if (adapter != null) {
            if (Operation.SERIALIZE == operation)
                return adapter.getAdapter().serialize(cast(object));
            else
                return adapter.getAdapter().deserialize(object);
        }

        return object;
    }

    private Map<?, ?> handleMap(Map<?, ?> map, Class<?> K, Class<?> V, SimpleYamlService yaml, Operation operation) {
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object kv = entry.getKey();
            Object vv = entry.getValue();
            TypeAdapter<?> keyAdapter = yaml.getTypeAdapterFor(K);
            TypeAdapter<?> valAdapter = yaml.getTypeAdapterFor(V);

            if (keyAdapter != null) {
                map.remove(kv);
                if (Operation.SERIALIZE == operation) kv = keyAdapter.getAdapter().serialize(cast(kv));
                else if (Operation.DESERIALIZE == operation) kv = keyAdapter.getAdapter().deserialize(kv);
                if (valAdapter != null) {
                    if (Operation.SERIALIZE == operation) vv = valAdapter.getAdapter().serialize(cast(vv));
                    else if (Operation.DESERIALIZE == operation) vv = valAdapter.getAdapter().deserialize(vv);
                }

                map.put(cast(kv), cast(vv));
                continue;
            }

            if (valAdapter != null) {
                if (Operation.SERIALIZE == operation) vv = valAdapter.getAdapter().serialize(cast(vv));
                else if (Operation.DESERIALIZE == operation) vv = valAdapter.getAdapter().deserialize(vv);
                map.put(cast(kv), cast(vv));
                continue;
            }

            map.put(cast(kv), cast(vv));
        }

        return map;
    }

    private Collection<?> handleCollection(Collection<?> collection, Class<?> generic, SimpleYamlService yaml, Operation operation) {
        TypeAdapter<?> adapter = yaml.getTypeAdapterFor(generic);
        if (adapter != null) {
            if (Operation.DESERIALIZE == operation) {
                return collection.stream()
                        .map(val -> adapter.getAdapter().deserialize(val))
                        .collect(Collectors.toList());
            }
            if (Operation.SERIALIZE == operation) {
                return collection.stream()
                        .map(val -> adapter.getAdapter().serialize(cast(val)))
                        .collect(Collectors.toList());
            }
        }
        return collection;
    }

    private Map<String, String> mapSetters(Class<?> clazz, Map<String, String> map, String path, NamingStrategy strategy) {
        for (Method method : clazz.getDeclaredMethods()) {
            Refers[] references = method.getAnnotationsByType(Refers.class);
            if (references.length == 0) {
                continue;
            }
            Refers reference = references[0];
            String name = path + strategy.apply(reference.value());
            map.put(method.getName(), path + name);
        }

        for (Class<?> nested : clazz.getDeclaredClasses()) {
            String name = strategy.apply(nested.getSimpleName());
            String cp = name + ".";
            Map<String, String> sub = mapSetters(nested, new LinkedHashMap<>(), cp, strategy);
            map.putAll(sub);
        }

        return map;
    }

    private <T> T cast(Object o) {
        return (T) o;
    }
}