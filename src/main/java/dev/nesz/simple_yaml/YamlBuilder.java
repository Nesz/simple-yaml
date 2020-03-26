package dev.nesz.simple_yaml;

import dev.nesz.simple_yaml.adapter.Adapter;
import dev.nesz.simple_yaml.adapter.TypeAdapter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedList;
import java.util.List;

public class YamlBuilder {

    protected DumperOptions dumper;
    protected Representer representer;
    protected List<TypeAdapter<?>> adapters;

    public YamlBuilder() {
        this.adapters = new LinkedList<>();
    }

    public YamlBuilder withDumper(DumperOptions dumper) {
        this.dumper = dumper;
        return this;
    }

    public YamlBuilder withRepresenter(Representer representer) {
        this.representer = representer;
        return this;
    }

    public <T> YamlBuilder withAdapter(Class<T> source, Adapter<T> adapter) {
        this.adapters.add(new TypeAdapter<>(source, adapter));
        return this;
    }

    public YamlService build() {
        return new YamlService(this);
    }

}
