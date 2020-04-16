package net.neszku.simple_yaml.helpers;

import net.neszku.simple_yaml.helpers.reflections.ReflectConstructor;

import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Method;

public class LookupHelper {

    public static Object lookupValue(Class<?> clazz, Method method) throws Throwable {
        return lookupValue(clazz, method, null);
    }

    public static Object lookupValue(Class<?> clazz, Method method, Object instance) throws Throwable {
        if (instance != null) {
            return method.invoke(instance);
        } else {
            instance = ProxyHelper.proxying(clazz);
        }
        ReflectConstructor<Lookup> constr = new ReflectConstructor<>(Lookup.class, Class.class, Integer.TYPE);
        return constr.newInstance(clazz, Lookup.PRIVATE)
                .unreflectSpecial(method, clazz)
                .bindTo(instance)
                .invokeWithArguments();

    }

}
