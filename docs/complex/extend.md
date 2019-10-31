# 自定义扩展

`JAVA动态链式` 查询 支持自定义扩展，主要继承SpecificationWrapper进行自定义方法扩展。

定义SpecificationWrapperExtend

```java
@NoArgsConstructor
public class SpecificationWrapperExtend<T> extends SpecificationWrapper<T> {
    /**
    * 自定义方法 (带判断条件的)
    */
    public SpecificationWrapper<T> customEq(boolean condition, String name, Object value) {
        return condition ? this.customEq(name, value) : this;
    }

    /**
    * 自定义方法 (调用handle方法，进行判断是否有关联查询，如果有的话进行LEFT_JOIN处理)
    */
    public SpecificationWrapper<T> customEq(String name, Object value) {
        return handle(name, e -> this.customEq(e, value));
    }

    /**
    * 自定义方法
    */
    public SpecificationWrapper<T> customEq(Expression<?> x, Object value) {
        super.getPredicates().add(super.getBuilder().equal(x, value));
        return this;
    }

    public SpecificationWrapperExtend(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder){
        super(root, query, builder);
    }
    /**
    * 如果有嵌套查询内部使用到自定义方法 则需要定义此方法
    */
    public SpecificationWrapperExtend<T> or_(Consumer<SpecificationWrapperExtend<T>> action) {
        return this.newWrapperExtend(false, action);
    }
    /**
    * 如果有嵌套查询内部使用到自定义方法 则需要定义此方法
    */
    public SpecificationWrapperExtend<T> and_(Consumer<SpecificationWrapperExtend<T>> action) {
        return this.newWrapperExtend(true, action);
    }
    /**
    * 如果有嵌套查询内部使用到自定义方法 则需要定义此方法
    */
    public SpecificationWrapperExtend<T> newWrapperExtend(boolean isConjunction, Consumer<SpecificationWrapperExtend<T>> action) {
        SpecificationWrapperExtend<T> specification = new SpecificationWrapperExtend(super.getRoot(),
                super.getQuery(), super.getBuilder());
        CriteriaBuilder newBuilder = specification.getBuilder();
        Predicate predicate = isConjunction ? newBuilder.conjunction() : newBuilder.disjunction();
        action.accept(specification);
        predicate.getExpressions().addAll(specification.getPredicates()) ;
        super.getPredicates().add(predicate);
        return this;
    }

}
```

定义 静态方法工具类 SpecificationsExtend

```java
@UtilityClass
public class SpecificationsExtend{

    public static <T> Specification<T> where(Consumer<SpecificationWrapperExtend<T>> action) {
        return Specifications.extendWhere(new SpecificationWrapperExtend(), action);
    }

    public static <T> Specification<T> conditionOf(Object object, Consumer<SpecificationWrapperExtend<T>> action) {
        return Specifications.extendConditionOf(object, new SpecificationWrapperExtend(), action);
    }

}
```

定义好上面两个扩展类之后，就可以进行查询了。
```java
Specification<Blog> spec = SpecificationsExtend.where(e->{
    e.customEq(Blog.Fields.id,1);
});
List<Blog> list = repository.findAll(spec);
```

```java
Specification<Blog> spec = SpecificationsExtend.conditionOf(vo, e->{
    e.customEq(Blog.Fields.status,1);
});
List<Blog> list = repository.findAll(spec);
```