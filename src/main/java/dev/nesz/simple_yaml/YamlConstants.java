package dev.nesz.simple_yaml;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.representer.Representer;

public class YamlConstants {

    public static final DumperOptions DEFAULT_DUMPER      = new DumperOptions();
    public static final Representer   DEFAULT_REPRESENTER = new Representer();

    static {
        DEFAULT_DUMPER.setIndent(2);
        DEFAULT_DUMPER.setAllowUnicode(true);
        DEFAULT_DUMPER.setSplitLines(false);
        DEFAULT_DUMPER.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        DEFAULT_DUMPER.setDefaultScalarStyle(DumperOptions.ScalarStyle.PLAIN);
        DEFAULT_REPRESENTER.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        DEFAULT_REPRESENTER.setDefaultScalarStyle(DumperOptions.ScalarStyle.DOUBLE_QUOTED);
    }

}
