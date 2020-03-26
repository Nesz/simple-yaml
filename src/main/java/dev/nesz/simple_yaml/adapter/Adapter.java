package dev.nesz.simple_yaml.adapter;

public interface Adapter<T> {

    default Object serialize(T object) {
        return null;
    }

    default T deserialize(Object object) {
        return null;
    }

}