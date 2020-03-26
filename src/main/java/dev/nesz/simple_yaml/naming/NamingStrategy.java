package dev.nesz.simple_yaml.naming;

import java.util.function.Function;

public enum NamingStrategy {

    HYPHEN_CASE(new Function<String, String>() {
        @Override
        public String apply(String to) {
            if (to.startsWith("get")) {
                to = to.substring(3);
            }

            StringBuilder out = new StringBuilder();
            char[] chars = to.toCharArray();
            chars[0] = Character.toLowerCase(chars[0]);
            for (int i = 0; i < to.length(); ++i) {
                char ch = chars[i];
                if (Character.isUpperCase(ch)) {
                    out.append("-").append(Character.toLowerCase(ch));
                    continue;
                }
                out.append(ch);
            }

            return out.toString();
        }
    }),

    CAMEL_CASE(new Function<String, String>() {
        @Override
        public String apply(String to) {
            if (to.startsWith("get")) {
                to = to.substring(3);
            }
            to = to.substring(0, 1).toUpperCase() + to.substring(1);
            return to;
        }
    });

    private Function<String, String> function;

    NamingStrategy(Function<String, String> function) {
        this.function = function;
    }


    public String apply(String to) {
        return function.apply(to);
    }

}
