package net.neszku.simple_yaml;

import net.neszku.simple_yaml.steorotype.Include;
import net.neszku.simple_yaml.naming.NamingStrategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class ProxyTrafficHandler implements InvocationHandler {

    private final SimpleYamlData data;
    private final Class<?> wrapped;
    private final NamingStrategy strategy;
    private final Map<String, String> setters;

    public ProxyTrafficHandler(SimpleYamlData data, Class<?> wrapped, NamingStrategy strategy, Map<String, String> setters) {
        this.data = data;
        this.wrapped = wrapped;
        this.setters = setters;
        this.strategy = strategy;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (method.isAnnotationPresent(Include.class)) {
            return proxy;
        }

        String setPath = setters.get(method.getName());
        if (setPath != null) {
            data.set(setPath, args[0]);
        }

        String methodName = strategy.apply(method.getName());
        String path = method.getDeclaringClass().toString();
        path = path.replaceAll("\\s+",""); //replace spaces
        path = path.replaceFirst("\\$", "");
        path = path.replaceAll("\\$", ".");
        path = path.replace("interface", "");
        path = splitByLastOccurrence(path, wrapped.getName());
        if (!path.isEmpty()) {
            path += ".";
        }
        path = strategy.apply(path);
        path += methodName;
        path = path.toLowerCase();

        return data.get(path);
    }

    private String splitByLastOccurrence(String path, String occ) {
        String[] split = path.split(occ);
        return split.length > 0 ? split[split.length - 1] : "";
    }
}