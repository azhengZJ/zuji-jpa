package top.springdatajpa.zujijpa.annotation;

import top.springdatajpa.zujijpa.enums.OperatorEnum;

import java.lang.annotation.*;

/**
 * @author azheng
 * @since 2019/9/30
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryOperator {
    OperatorEnum value() default OperatorEnum.EQUAL;
    String fieldName() default "";
}
