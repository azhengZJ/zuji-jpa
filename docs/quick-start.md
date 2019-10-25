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

### 单层多条件查询（入参定义式零逻辑）

首先要定义查询入参的实体类，如果字段为null则不参与条件查询，默认使用的是equal(=)，如果是Collection类型的字段，默认使用的是 IN 查询，
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
     *  此注解等同于SQL: title like "#{title}%"
     */
    @QueryOperator(Operator.STARTING_WITH)
    private String title;
    /**
     *  属于Collection类型，所以默认使用的是IN查询
     */
    private List<String> author;
    /**
     *  此注解等同于SQL: content like "%#{content}%"
     */
    @QueryOperator(Operator.CONTAINS)
    private String content;
    /**
     *  无注解默认使用的是等于
     */
    private Integer status;
   
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
        Specification<Blog> spec = SpecificationUtils.conditionOf(query);
        return repository.findAll(spec);
    }
}
```

等同于如下sql，如果字段值为NULL，则不参与条件查询。
```sql
SELECT 
    *
FROM
    blog
WHERE
    title LIKE 'zuji%'
        AND author IN ('azheng1' , 'azheng2')
        AND content LIKE '%内容%'
        AND status = 0;
```


### 多层嵌套复杂条件查询（极简java动态链式）

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
       Specification<User> spec = SpecificationUtils.where(e -> {
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
如果没有条件判断也可以写成这样，链式编程

```java
@RestController
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private UserRepository repository;
    
    @GetMapping("/list")
    public Page<User> list(ReqUserListVO params) {
       Specification<User> spec = SpecificationUtils.where(e -> {
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

