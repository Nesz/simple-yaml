package dev.nesz.simple_yaml.helpers.reflect;

import dev.nesz.simple_yaml.naming.NamingStrategy;
import dev.nesz.simple_yaml.naming.PropertyNamingStrategy;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class ReflectionHelper {

    public static List<Class<?>> getGenericReturnType(Method method) {
        List<Class<?>> types = new LinkedList<>();

        Type returnType = method.getGenericReturnType();
        try {
            if (returnType instanceof ParameterizedType) {
                Type[] parametrizedTypes = (((ParameterizedType) returnType)
                        .getActualTypeArguments());

                for (Type type : parametrizedTypes) {
                    types.add(Class.forName(type.getTypeName()));
                }
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return types;
    }

    public static NamingStrategy getStrategy(Class<?> clazz) {
        PropertyNamingStrategy[] strategies = clazz.getAnnotationsByType(PropertyNamingStrategy.class);
        if (strategies.length == 0) {
            return NamingStrategy.HYPHEN_CASE;
        }

        return strategies[0].value();
    }

}
