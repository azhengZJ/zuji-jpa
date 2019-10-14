package top.springdatajpa.zujijpa.wrapper;

import lombok.Data;

/**
 * @author azheng
 * @since 2019/9/30
 */
@Data
public class OperatorWrapper {
    private SpecificationWrapper<?> specWrapper;
    private String name;
    private Object value;

    public <Y extends Comparable<? super Y>> Y getCompareValue(){
        return (Y)value;
    }
}
