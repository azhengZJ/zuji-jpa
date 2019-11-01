# 快速开始

Zuji-JPA查询全部基于Specification进行扩展增强开发，所以使用之前必须要继承JpaSpecificationExecutor接口
```java
public interface BlogRepository extends JpaSpecificationExecutor<Blog> {

}
```

或者使用Zuji-JPA自带的BaseRepository，BaseRepository继承了JpaSpecificationExecutor和JpaRepository接口。
```java
public interface BlogRepository extends BaseRepository<Blog> {
    
}
```

## 1、单层多条件查询
> 入参定义式零逻辑

入参定义式查询仅支持单层条件查询，支持JOIN，支持equal、like、in、between等这些常用的查询关键字，多层嵌套复杂查询请参考下一节java动态链式查询。

首先要定义查询入参的实体类，如果字段为NULL则不参与条件查询，默认使用的是等于equal，如果是Collection类型的字段，默认使用的是 IN 查询，
也可以使用@QueryOperator 注解里面的 `fieldName` 字段来定义对应数据库的字段名称。

  
```java
/**
 *  如果字段为null则不参与条件查询，默认使用的是等于equal，
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
        AND b.content LIKE '%value%'
        AND b.status = 0
        AND a.user_name = 'azheng'
```


## 2、多层嵌套复杂条件查询
> java动态链式

此查询类似于mybatis-plus的条件构造器。
以下示例包含：动态条件查询 + or 嵌套条件+ 排序+ 分页

```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepository repository;
    
    @GetMapping("/list")
    public Page<User> list(ReqUserListVO params) {
       Specification<User> spec = Specifications.where(e -> {
           if (!params.getUserType().equals(UserType.ALL)) {
               e.eq("userType", params.getUserType());
           }
           if (params.getUserName() != null) {
               e.contains("userName", params.getUserName());
           }
           if (params.getRange() != null) {
               e.eq("assigneeId", AuthHelper.currentUserId());
           }
           e.or(e2 -> {
               e2.eq("status", "1");
               e2.eq("status", "2");
           });
           e.eq("deleted", 0);
       });
       Sort sort = Sort.by("createTime").descending();
       return repository.findAll(spec, params.pageRequest(sort));
    }
}
```

判断条件也可以放在方法里面，看起来更简洁一点，如下
```java
public Page<User> list(ReqUserListVO params) {
   Specification<User> spec = Specifications.where(e -> {
       e.eq(!params.getUserType().equals(UserType.ALL), "userType", params.getUserType());
       e.contains(params.getUserName() != null, "userName", params.getUserName());
       e.eq(params.getRange() != null, "assigneeId", AuthHelper.currentUserId());
       e.or(e2 -> {
           e2.eq("status", "1");
           e2.eq("status", "2");
       });
       e.eq("deleted", 0);
   });
   Sort sort = Sort.by("createTime").descending();
   return repository.findAll(spec, params.pageRequest(sort));
}
```

如果没有条件判断也可以写成这样，链式编程

```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepository repository;
    
    @GetMapping("/list")
    public Page<User> list(ReqUserListVO params) {
       Specification<User> spec = Specifications.where(e -> {
           e.eq("userType", params.getUserType())
            .contains("userName", params.getUserName())
            .eq("assigneeId", AuthHelper.currentUserId())
            .or(e2 -> e2.eq("status", "1").eq("status", "2"))
            .eq("deleted", 0);
       });
       Sort sort = Sort.by("createTime").descending();
       return repository.findAll(spec, params.pageRequest(sort));
    }
}
```

等同于sql

```sql
SELECT
	* 
FROM
	user
WHERE
	user_type = 'ADMIN' 
	AND user_name like "%admin%" 
	AND assignee_id = 11 
	AND ( status = 1 OR status = 2 ) 
	AND deleted = 0 
ORDER BY
	create_time DESC 
	LIMIT 0,10
```

> **注**：如果第一层（最外面层）是OR关联查询，调用where方法的时候需要添加一个参数isConjunction，为true的时候为and连接（默认为true），为false的时候为or连接。

```java
Specification<Blog> spec = Specifications.where(false, e -> {
    e.eq(Blog.Fields.status, 0);
    e.eq(Blog.Fields.status, 1);
});
```
```sql
select * from blog where status = 0 or status = 1;
```

> **注**：上述示例中查询的字段名使用的是字符串，字符串是不被检查的，很容易出错。lombok提供了可以生成和属性名一样的的静态字段内部类的注解，实体类上面添加@FieldNameConstants注解即可使用。

```java
//...省略
@FieldNameConstants
public class Blog {
    //...省略
}
```

```java
public Page<User> list(ReqUserListVO params) {
   Specification<User> spec = Specifications.where(e -> {
       e.eq(User.Fields.userType, params.getUserType())
        .contains(User.Fields.userName, params.getUserName())
        .eq(User.Fields.assigneeId, AuthHelper.currentUserId())
        .or(e2 -> e2.eq(User.Fields.status, "1").eq(User.Fields.status, "2"))
        .eq(User.Fields.deleted, 0);
   });
   Sort sort = Sort.by("createTime").descending();
   return repository.findAll(spec, params.pageRequest(sort));
}
```

## 3、两者结合使用

Zuji-Jpa支持将`入参定义式`和`JAVA动态链式`两者结合在一起使用，可以面对更多复杂的场景。

首先需要定义入参实体类，具体参考[入参定义式](#_1%e3%80%81%e5%8d%95%e5%b1%82%e5%a4%9a%e6%9d%a1%e4%bb%b6%e6%9f%a5%e8%af%a2)，定义好之后直接进行查询；

```java
@PostMapping("/list")
public Object list(@RequestBody ReqBlogQueryVO query){
    Specification<Blog> spec = Specifications.conditionOf(query,e -> {
        e.eq(Blog.Fields.deleted, 0);
        // 如上，在此添加需要的查询，参考第二节 java动态链式 查询
    });
    return repository.findAll(spec);
}
```

## 4、自定义查询返回

### Dynamic Projections（动态投影）

数据库查询大部分时候都要 返回自定义字段（ VO/DTO ），Spring Data Jpa 默认支持 泛型 动态投影（Projections） ，投影的类型可以是接口也可以是类。

Repository查询接口定义
```java
interface PersonRepository extends Repository<Person, UUID> {
  <T> Collection<T> findByUsername(String username, Class<T> type);
}
```
或者
```java
interface PersonRepository extends Repository<Person, UUID> {
  NamesQueryDTO findByUsername(String username, Class<T> type);
}
```

**基于接口的投影**，须提供字段对应的get方法接口。

```java
interface NamesQueryDTO {

  String getFirstname();
  String getLastname();
  
}
```
**基于类的投影**，定义好所有查询字段，并且提供全参构造器。强烈推荐 使用Lombok的@Value注解 简化代码。

```java
@Value
class NamesOnlyDTO {
	String firstname, lastname;
}
```

### 提供工具类转换

动态投影有时候会不生效，这时候就需要自己进行类型转换，zuji-jpa提供了相关工具类。

没有分页的列表
```java
List<Blog> list = repository.findAll(spec);
return EntityUtils.cast(list, RespBlogVO.class);
```

有分页的列表
```java
Specification<Blog> spec = Specifications.conditionOf(vo, e->{
    e.eq(Blog.Fields.status, 1);
});
PageRequest page = vo.pageRequest(Sort.by(Blog.Fields.id).descending());
Page<Blog> list = repository.findAll(spec, page);
return JpaHelper.castPage(list, page, RespBlogVO.class);
```
或者

```java
Specification<Blog> spec = Specifications.conditionOf(vo, e->{
    e.eq(Blog.Fields.status, 1);
});
Page<Blog> list = repository.findAll(spec, vo.pageRequest(Sort.by(Blog.Fields.id).descending()));
Page<RespBlogVO> result = JpaHelper.castPage(list, RespBlogVO.class);
```

单个对象
```java
Blog blog = repository.findOne(id);
RespBlogVO result =  EntityUtils.cast(Blog, RespBlogVO.class);
```