package net.neszku.simple_yaml.adapter;

public interface Adapter<T> {

    /**
     * Serializes the object
     *
     * @param object the object to serialize
     * @return serialized object
     */
    default Object serialize(T object) {
        return null;
    }

    /**
     * Deserializes the object
     *
     * @param object the object to deserialize
     * @return deserialized object
     */
    default T deserialize(Object object) {
        return null;
    }

}