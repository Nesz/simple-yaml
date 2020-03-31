package dev.nesz.simple_yaml.helpers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ProxyHelper {

    private static final InvocationHandler FAKE_PROXY = (proxy, method, args) -> null;

    public static <T> T proxyingWithSubInterfaces(Class<T> clazz) {
        return proxyingWithSubInterfaces(clazz, FAKE_PROXY);
    }

    public static <T> T proxyingWithSubInterfaces(Class<T> clazz, InvocationHandler handler) {
        Class<?>[] interfaces = getInterfaces(clazz).toArray(new Class<?>[0]);
        return proxying(clazz, interfaces, handler);
    }

    public static <T> T proxying(Class<T> clazz) {
        return proxying(clazz, new Class<?>[] { clazz }, FAKE_PROXY);
    }

    public static <T> T proxying(Class<T> clazz, InvocationHandler handler) {
        return proxying(clazz, new Class<?>[] { clazz }, FAKE_PROXY);
    }

    @SuppressWarnings("unchecked")
    public static <T> T proxying(Class<T> clazz, Class<?>[] interfaces, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                interfaces,
                handler);
    }

    public static List<Class<?>> getInterfaces(Class<?> clazz) {
        List<Class<?>> interfaces = new LinkedList<>();

        interfaces.add(clazz);
        for (Class<?> sub : clazz.getDeclaredClasses()) {
            if (!sub.isInterface()) {
                continue;
            }
            interfaces.addAll(getInterfaces(sub));
        }

        return interfaces;
    }

}
