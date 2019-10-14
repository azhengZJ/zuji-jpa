package top.springdatajpa.zujijpa.enums;

import top.springdatajpa.zujijpa.wrapper.OperatorWrapper;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author azheng
 * @since 2019/9/30
 */
public enum OperatorEnum {
    EQUAL(e -> e.getSpecWrapper().eq(e.getName(), e.getValue())),
    LIKE(e -> e.getSpecWrapper().like(e.getName(), (String)e.getValue())),
    STARTING_WITH(e -> e.getSpecWrapper().startingWith(e.getName(), (String)e.getValue())),
    ENDING_WITH(e -> e.getSpecWrapper().endingWith(e.getName(), (String)e.getValue())),
    CONTAINS(e -> e.getSpecWrapper().contains(e.getName(), (String)e.getValue())),
    GREATER_THAN_EQUAL_TO(e -> e.getSpecWrapper().greaterThanOrEqualTo(e.getName(), e.getCompareValue())),
    LESS_THAN_EQUAL_TO(e -> e.getSpecWrapper().lessThanOrEqualTo(e.getName(), e.getCompareValue())),
    GREATER_THAN(e -> e.getSpecWrapper().greaterThan(e.getName(), e.getCompareValue())),
    LESS_THAN(e -> e.getSpecWrapper().lessThan(e.getName(), e.getCompareValue())),
    IN(e -> e.getSpecWrapper().in(e.getName(), (Collection<?>) e.getValue()))
    ;
    private Consumer<OperatorWrapper> consumer;
    OperatorEnum(Consumer<OperatorWrapper> consumer){
            this.consumer = consumer;
    }
    public Consumer<OperatorWrapper> consumer(){
        return this.consumer;
    }
}
