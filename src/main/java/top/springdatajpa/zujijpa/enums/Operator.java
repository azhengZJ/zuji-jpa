package top.springdatajpa.zujijpa.enums;

import top.springdatajpa.zujijpa.wrapper.OperatorWrapper;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author azheng
 * @since 2019/9/30
 */
public enum Operator {
    /**
     * =
     */
    EQ(e -> e.getSpecWrapper().eq(e.getName(), e.getValue())),
    /**
     * like "#{value}"
     */
    LIKE(e -> e.getSpecWrapper().like(e.getName(), (String)e.getValue())),
    /**
     * like "#{value}%"
     */
    STARTING_WITH(e -> e.getSpecWrapper().startingWith(e.getName(), (String)e.getValue())),
    /**
     * like "%#{value}"
     */
    ENDING_WITH(e -> e.getSpecWrapper().endingWith(e.getName(), (String)e.getValue())),
    /**
     * like "%#{value}%"
     */
    CONTAINS(e -> e.getSpecWrapper().contains(e.getName(), (String)e.getValue())),
    /**
     * >=
     */
    GT_OR_EQ(e -> e.getSpecWrapper().gtOrEq(e.getName(), e.getCompareValue())),
    /**
     * <=
     */
    LT_OR_EQ(e -> e.getSpecWrapper().ltOrEq(e.getName(), e.getCompareValue())),
    /**
     * >
     */
    GT(e -> e.getSpecWrapper().gt(e.getName(), e.getCompareValue())),
    /**
     * <
     */
    LT(e -> e.getSpecWrapper().lt(e.getName(), e.getCompareValue())),
    /**
     * in (#{collection})
     */
    IN(e -> e.getSpecWrapper().in(e.getName(), (Collection<?>) e.getValue()))
    ;
    private Consumer<OperatorWrapper> consumer;
    Operator(Consumer<OperatorWrapper> consumer){
            this.consumer = consumer;
    }
    public Consumer<OperatorWrapper> consumer(){
        return this.consumer;
    }
}
