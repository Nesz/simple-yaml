package net.neszku.simple_yaml.helpers;

import net.neszku.simple_yaml.helpers.reflections.ReflectionHelper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class ProxyHelper {

    private static final InvocationHandler FAKE_PROXY = (proxy, method, args) -> null;

    public static <T> T proxyingWithSubInterfaces(Class<T> clazz) {
        return proxyingWithSubInterfaces(clazz, FAKE_PROXY);
    }

    public static <T> T proxyingWithSubInterfaces(Class<T> clazz, InvocationHandler handler) {
        Class<?>[] interfaces = ReflectionHelper.getInterfaces(clazz).toArray(new Class<?>[0]);
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
}
