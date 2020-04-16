package net.neszku.simple_yaml;

import net.neszku.simple_yaml.adapter.Adapter;
import net.neszku.simple_yaml.adapter.TypeAdapter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

import java.util.LinkedList;
import java.util.List;

public class SimpleYamlBuilder {

    protected DumperOptions dumper;
    protected Representer representer;
    protected List<TypeAdapter<?>> adapters;

    public SimpleYamlBuilder() {
        this.adapters = new LinkedList<>();
    }

    public SimpleYamlBuilder withDumper(DumperOptions dumper) {
        this.dumper = dumper;
        return this;
    }

    public SimpleYamlBuilder withRepresenter(Representer representer) {
        this.representer = representer;
        return this;
    }

    public <T> SimpleYamlBuilder withAdapter(Class<T> source, Adapter<T> adapter) {
        this.adapters.add(new TypeAdapter<>(source, adapter));
        return this;
    }

    public SimpleYamlService build() {
        return new SimpleYamlService(this);
    }

}
