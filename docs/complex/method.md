# API 方法

## SpecificationWrapper

这是动态链式最核心的包装工具类，里面包含了所有的API方法。所有方法参数name都支持JOIN关联查询，例如`createUser.userName` 写法，前提是需要在映射实体类里面维护好关联关系。


## and 和 or
这两个方法主要是做嵌套复杂查询使用的。当使用这个方法时，方法里面的函数查询都会通过这个关键字进行关联。可以无限循环嵌套。
```java
Specification<Blog> spec = Specifications.where(e -> {
    e.and(e2 -> {
        e2.eq(Blog.Fields.title,"aaa");
        e2.eq(Blog.Fields.title,"bbb");
    });
    e.or(e2 -> {
        e2.contains(Blog.Fields.content,"ccc");
        e2.contains(Blog.Fields.content,"ddd");
        e2.and(e3 -> {
            e3.eq(Blog.Fields.author,"eee");
            e3.eq(Blog.Fields.author,"fff");
        });
    });
    e.eq("status",0);
});
```
等同于sql
```sql
SELECT 
    *
FROM
    blog
WHERE
    (title = 'aaa' AND title = 'bbb')
        AND (content LIKE '%ccc%'
			OR content LIKE '%ddd%'
			OR (author = 'eee' AND author = 'fff'));
```

## eq
```java
eq(boolean condition, String name, Object value)
eq(String name, Object value)
```
等于=
- 例：`eq("name", "张三")`  --->  `name = '张三'`

## ne
```java
ne(boolean condition, String name, Object value)
ne(String name, Object value)
```
不等于 <>
- 例：`ne("name", "张三")`  --->  `name <> '张三'`

## like
```java
like(boolean condition, String name, String value)
like(String name, String value)
```
like '值'
- 例：`like("name", "张三")`  --->  `name like '张三'`

## contains
```java
contains(boolean condition, String name, String value)
contains(String name, String value)
```
like '%值%'
- 例：`contains("name", "张三")`  --->  `name like '%张三%'`

## startingWith
```java
startingWith(boolean condition, String name, String value)
startingWith(String name, String value)
```
like '值%'
- 例：`startingWith("name", "张三")`  --->  `name like '张三%'`

## endingWith
```java
endingWith(boolean condition, String name, String value)
endingWith(String name, String value)
```
like '%值'
- 例：`endingWith("name", "张三")`  --->  `name like '%张三'`

## ge
```java
ge(boolean condition, String name, Object value)
ge(String name, Object value)
```
大于或等于 >=
- 例：`ge("age", 18)`  --->  `age >= 18`

## le
```java
le(boolean condition, String name, Object value)
le(String name, Object value)
```
小于或等于 <=
- 例：`le("age", 18)`  --->  `age <= 18`

## gt
```java
gt(boolean condition, String name, Object value)
gt(String name, Object value)
```
大于 >
- 例：`gt("age", 18)`  --->  `age > 18`

## lt
```java
lt(boolean condition, String name, Object value)
lt(String name, Object value)
```
小于 <
- 例：`lt("age", 18)`  --->  `age < 18`

## in

```java
in(boolean condition, String name, Collection<?> value)
in(String name, Collection<?> value)
```
IN (value.get(0), value.get(1), ...) 
- 例：`in("age", {1,2,3})`  --->  `age in (1,2,3)`

```java
in(boolean condition, String name, Object... value)
in(String name, Object... value)
```
IN (v0, v1, ...) 
- 例：`in("age", 1, 2, 3)`  --->  `age in (1,2,3)`

## notIn

```java
notIn(boolean condition, String name, Collection<?> value)
notIn(String name, Collection<?> value)
```
NOT IN (value.get(0), value.get(1), ...)   
- 例：`notIn("age", {1,2,3})`  --->  `age not in (1,2,3)`

```java
notIn(boolean condition, String name, Object... value)
notIn(String name, Object... value)
```
NOT IN (v0, v1, ...)   
- 例：`notIn("age", 1, 2, 3)`  --->  `age not in (1,2,3)`


## between
```java
between(boolean condition, String name, Object start, object end)
between(String name, Object start, object end)
```
BETWEEN 值1 AND 值2
- 例：`between("age", 18 ,40)`  --->  `age between 18 and 40`

## isNull
```java
isNull(boolean condition, String name)
isNull(String name)
```
字段 IS NULL
- 例：`isNull("name")`  --->  `name is null`

## isNotNull
```java
isNotNull(boolean condition, String name)
isNotNull(String name)
```
字段 IS NOT NULL
- 例：`isNotNull("name")`  --->  `name is not null`

