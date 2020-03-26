package dev.nesz.simple_yaml;

import dev.nesz.simple_yaml.annotations.Include;
import dev.nesz.simple_yaml.naming.NamingStrategy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class ProxyTrafficHandler implements InvocationHandler {

    private final YamlData data;
    private final NamingStrategy strategy;
    private final Map<String, String> setters;

    public ProxyTrafficHandler(YamlData data, NamingStrategy strategy, Map<String, String> setters) {
        this.data = data;
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
        path = path.replace("$", ".").toLowerCase();
        path += "." + methodName;
        path = path.substring(path.indexOf(".") + 1);

        return data.get(path);
    }
}