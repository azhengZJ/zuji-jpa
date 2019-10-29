# 总体示例

##多层嵌套复杂条件查询
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
    e.eq(Blog.Fields.deleted,query.getAuthor());
});
return repository.findAll(spec);
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