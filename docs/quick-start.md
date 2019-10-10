# 快速开始

Zuji-JPA查询全部基于Specification进行开发，所以使用之前必须要继承JpaSpecificationExecutor接口
```java
public interface UserRepository extends JpaSpecificationExecutor<User> {

}
```

### 多层嵌套复杂条件查询

此查询类似于mybatis-plus的条件构造器。
以下示例包含：动态条件查询 + or 嵌套条件+ 排序+ 分页

```java
public Page<User> list(ReqUserListDTO params) {
   Specification<User> spec = SpecificationUtils.where(e -> {
       if (!params.getUserType().equals(UserType.ALL)) {
           e.eq("userType", params.getUserType());
       }
       if (params.getPriority() > 0) {
           e.eq("priority", params.getPriority());
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
```
如果没有条件判断也可以写成这样，链式编程

```java
public Page<User> list(ReqUserListDTO params) {
   Specification<User> spec = SpecificationUtils.where(e -> {
       if (!params.getUserType().equals(UserType.ALL)) {
           e.eq("userType", params.getUserType());
       }
       if (params.getPriority() > 0) {
           e.eq("priority", params.getPriority());
       }
       if (params.getRange() != null) {
           e.eq("assigneeId", AuthHelper.currentUserId());
       }
       e.or(e2 -> e2.eq("status", "1").eq("status", "2")).eq("deleted", 0);
   });
   Sort sort = Sort.by("createTime").descending();
   return repository.findAll(spec, params.pageRequest(sort));
}
```

等同于sql

```sql
SELECT
	* 
FROM
	user
WHERE
	userType = 'ADMIN' 
	AND priority = 1 
	AND assigneeId = 11 
	AND ( STATUS = 1 OR STATUS = 2 ) 
	AND deleted = 0 
ORDER BY
	create_time DESC 
	LIMIT 0,10
```

### 单层多条件查询

未完待续。。。敬请期待