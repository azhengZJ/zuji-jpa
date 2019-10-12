package top.springdatajpa.zujijpa.annotation;

import java.lang.annotation.*;

/**
 * @author azheng
 * @date 2019/9/30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryIgnore {
}
