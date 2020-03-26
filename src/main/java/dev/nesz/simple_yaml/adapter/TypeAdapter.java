package dev.nesz.simple_yaml.adapter;

public class TypeAdapter<T> {

    private final Class<T> type;
    private final Adapter<T> adapter;

    public TypeAdapter(Class<T> type, Adapter<T> adapter) {
        this.type = type;
        this.adapter = adapter;
    }

    public Class<T> getType() {
        return type;
    }

    public Adapter<T> getAdapter() {
        return adapter;
    }

    @Override
    public String toString() {
        return type.getSimpleName() + " = " + adapter.getClass().getSimpleName();
    }
}