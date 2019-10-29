# 入参定义

## @QueryOperator

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryOperator {
    Operator value() default Operator.EQ;
    String fieldName() default "";
}
```

```java

@QueryOperator(Operator.CONTAINS)
private String name;

```

这是定义入参实体类最核心的注解，value指定查询关键字，默认为equal，fieldName 指定查询映射实体类的字段名称，默认为字段名。


## Operator

这是QueryOperator注解里value值的枚举。

```java
public enum Operator {
    /**
     * 等于 =
     */
    EQ,
    /**
     * 不等于 <>
     */
    NE,
    /**
     * like "值"
     */
    LIKE,
    /**
     * like "值%"
     */
    STARTING_WITH,
    /**
     * like "%值"
     */
    ENDING_WITH,
    /**
     * like "%值%"
     */
    CONTAINS,
    /**
     * 大于等于 >=
     */
    GE,
    /**
     * 小于等于 <=
     */
    LE,
    /**
     * 大于 >
     */
    GT,
    /**
     * 小于 <
     */
    LT,
    /**
     * in (集合类型值)
     */
    IN,
    /**
     * not in (集合类型值)
     */
    NOT_IN
    ;
}
```

## @QueryIgnore

忽略查询的字段可以使用此注解。