package net.neszku.simple_yaml.helpers.reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ReflectConstructor<T> {

    private final Class<?> clazz;
    private final Class<?>[] parameterTypes;
    private Constructor<?> constructor;

    public ReflectConstructor(Class<?> clazz, Class<?>... parameterTypes) {
        this.clazz = clazz;
        this.parameterTypes = parameterTypes;
    }

    private void init() throws NoSuchMethodException {
        if (constructor == null) {
            constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
        }
    }

    @SuppressWarnings("unchecked")
    public T newInstance(Object... initargs) throws IllegalAccessException, InvocationTargetException,
                                                    InstantiationException, NoSuchMethodException {
        init();
        return (T) constructor.newInstance(initargs);
    }

}