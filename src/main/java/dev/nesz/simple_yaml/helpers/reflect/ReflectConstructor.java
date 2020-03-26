package dev.nesz.simple_yaml.helpers.reflect;

import java.lang.reflect.Constructor;

public class ReflectConstructor<T> {

    private final Class<?> clazz;
    private final Class<?>[] parameterTypes;
    private Constructor<?> constructor;

    public ReflectConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        this.clazz = clazz;
        this.parameterTypes = parameterTypes;
    }

    private void init() throws Exception {
        if (constructor == null) {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        }
    }

    @SuppressWarnings("unchecked")
    public T newInstance(Object... initargs) throws Exception {
        init();
        return (T) constructor.newInstance(initargs);
    }

}