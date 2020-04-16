package net.neszku.simple_yaml;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleYamlData {

    private static final int END = -1;

    protected final Map<String, Object> dataMap;

    public SimpleYamlData(Map<String, Object> dataMap) {
        this.dataMap = dataMap;
    }

    @SuppressWarnings("unchecked")
    public Object get(String path) {
        int beg = END, end;
        Map<String, Object> section = dataMap;
        while ((beg = path.indexOf('.', end = beg + 1)) != END) {
            section = (Map<String, Object>) section.get(path.substring(end, beg));
            if (section == null) {
                return null;
            }
        }

        String key = path.substring(end);
        return section == dataMap ? dataMap.get(key) : section.get(key);
    }

    @SuppressWarnings("unchecked")
    public void set(String path, Object value) {
        int beg = END, end;
        String key;
        Map<String, Object> section = dataMap;
        while ((beg = path.indexOf('.', end = beg + 1)) != END) {
            key = path.substring(end, beg);
            Map<String, Object> subSection = (Map<String, Object>) section.get(key);
            if (subSection == null) {
                section.put(key, new LinkedHashMap<>());
            } else {
                section = subSection;
            }
        }

        key = path.substring(end);
        if (section == dataMap) {
            dataMap.put(key, value);
        } else {
            section.put(key, value);
        }
    }

    @Override
    public String toString() {
        return "YamlData{" +
                "dataMap=" + dataMap +
                '}';
    }
}
