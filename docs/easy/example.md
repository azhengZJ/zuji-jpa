# 总体示例

## 单层多条件查询
> 入参定义式零逻辑

入参定义式查询仅支持单层条件查询，支持JOIN，支持equal、like、in、between等这些常用的查询关键字，多层嵌套复杂查询请参考下一节java动态链式查询。

首先要定义查询入参的实体类，如果字段为NULL则不参与条件查询，默认使用的是equal(=)，如果是Collection类型的字段，默认使用的是 IN 查询，
也可以使用@QueryOperator 注解里面的 `fieldName` 字段来定义对应数据库的字段名称。

  
```java
/**
 *  如果字段为null则不参与条件查询，默认使用的是equal(=)，
 *  如果是Collection类型的字段，默认使用的是 IN 查询
 */
@Data
public class ReqBlogQueryVO {
    /**
     *  此注解用于忽略查询
     */
    @QueryIgnore
    private Long id;
    /**
     *  此注解等同于SQL: title like "value%"
     */
    @QueryOperator(Operator.STARTING_WITH)
    private String title;
    /**
     *  属于Collection类型，所以默认使用的是IN查询
     */
    private List<String> author;
    /**
     *  此注解等同于SQL: content like "%value%"
     */
    @QueryOperator(Operator.CONTAINS)
    private String content;
    /**
     *  无注解默认使用的是等于
     */
    private Integer status;
    /**
     *  LEFT_JOIN的方式有两种，下面使用的是第二种
     */
    @JoinColumn(name="createUser")
    @QueryOperator(Operator.CONTAINS)
    private String userName;

   
}
```

入参定义式查询支持JOIN，LEFT_JOIN的方式有两种，以下任选其一即可。

  1、@QueryOperator注解指定实体类JOIN别名，如 @QueryOperator(fieldName="createUser.userName",value=Operator.CONTAINS)
  
  2、@JoinColumn注解指定JOIN的关联字段名，如 @JoinColumn(name="createUser")
  
由于Spring Data Jpa是基于Hibernate开发的，所以JOIN还是继承了Hibernate面向对象的方式，需要在实体类里面定义好关联关系。

> **注**：如果开启了自动建表而又不想在数据库创建外键关联的话需要加上注解  
    @JoinColumn(foreignKey = @ForeignKey(NO_CONSTRAINT))。

```java
@Data
@Entity
@FieldNameConstants
public class Blog {
    //......其他属性省略
    /**
     * 定义和User表的关联关系
     */
    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(NO_CONSTRAINT))
    private User createUser;

}
```


定义好之后就可以直接使用入参实体类生成查询条件，进行查询。

```java
@RestController
@RequestMapping("/blog")
public class BlogController {

    @Autowired
    private BlogRepository repository;

    @PostMapping("/list")
    public Object list(@RequestBody ReqBlogQueryVO query){
        Specification<Blog> spec = Specifications.conditionOf(query);
        return repository.findAll(spec);
    }
}
```

等同于如下sql，如果字段值为NULL，则不参与条件查询。

```sql
SELECT 
    b.*
FROM
    blog b left outer join user u on b.createUserId = u.id
WHERE
    b.title LIKE 'zuji%'
        AND b.author IN ('azheng1' , 'azheng2')
        AND b.content LIKE '%博客%'
        AND b.status = 0
        AND a.user_name = 'azheng'
```

