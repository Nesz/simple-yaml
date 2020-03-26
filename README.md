## Usage

```java
@PropertyNamingStrategy(NamingStrategy.HYPHEN_CASE)
public interface TestConfig {

    default Motor getMotor() {
        return new Motor(1, "nazwa");
    }

    @Refers("getMotor")
    void setMotor(Motor motor);

    @Include
    Mysql getMysql();
    interface Mysql {
        default String getHost() {
            return "getHost";
        }
    }
}
```

```java
public class MotorAdapter implements Adapter<Motor> {

    @Override
    public Object serialize(Motor motor) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", motor.getId());
        map.put("name", motor.getName());
        return map;
    }

    @Override
    public Motor deserialize(Object object) {
        Map<String, Object> map = (Map<String, Object>) object;
        return new Motor((int)map.get("id"), (String)map.get("name"));
    }
}
```

```java
YamlService yaml = new YamlBuilder()
        .withAdapter(Motor.class, new MotorAdapter())
        .withDumper(YamlConstants.DEFAULT_DUMPER)
        .withRepresenter(YamlConstants.DEFAULT_REPRESENTER)
        .build();

String dump = yaml.dump(TestConfig.class);
TestConfig tst = yaml.load(TestConfig.class, dump);
tst.setMotor(new Motor(3, "name"));
yaml.dump(TestConfig.class, tst); //dump with changed value
```