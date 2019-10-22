package top.springdatajpa.zujijpa.annotation;

import top.springdatajpa.zujijpa.enums.Operator;

import java.lang.annotation.*;

/**
 * @author azheng
 * @since 2019/9/30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryOperator {
    Operator value() default Operator.EQUAL;
    String fieldName() default "";
}
