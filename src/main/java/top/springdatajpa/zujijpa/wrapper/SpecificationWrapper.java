package top.springdatajpa.zujijpa.wrapper;

import lombok.Getter;
import lombok.Setter;
import top.springdatajpa.zujijpa.Specifications;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author azheng
 * @since 2019/5/7
 */
@Getter
@Setter
public class SpecificationWrapper<T> {

    private Root<T> root;
    private CriteriaQuery<?> query;
    private CriteriaBuilder builder;

    private List<Predicate> predicates = new ArrayList<>();

    private static final String SEPARATOR = ".";

    public SpecificationWrapper(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        this.root = root;
        this.query = query;
        this.builder = builder;
    }

    public SpecificationWrapper<T> or(Consumer<SpecificationWrapper<T>> action) {
        return this.newWrapper(false, action);
    }

    public SpecificationWrapper<T> and(Consumer<SpecificationWrapper<T>> action) {
        return this.newWrapper(true, action);
    }

    public SpecificationWrapper<T> newWrapper(boolean isConjunction,
                                              Consumer<SpecificationWrapper<T>> action) {
        SpecificationWrapper<T> specification = new SpecificationWrapper<>(root, query, builder);
        CriteriaBuilder newBuilder = specification.getBuilder();
        Predicate predicate = isConjunction ? newBuilder.conjunction() : newBuilder.disjunction();
        action.accept(specification);
        predicate.getExpressions().addAll(specification.getPredicates()) ;
        predicates.add(predicate);
        return this;
    }

    public SpecificationWrapper<T> eq(boolean condition, String name, Object data) {
        if(condition){
            this.eq(name, data);
        }
        return this;
    }

    public SpecificationWrapper<T> eq(String name, Object data) {
        return handle(name, e -> this.eq(e, data));
    }

    public SpecificationWrapper<T> eq(Expression<?> x, Object data) {
        predicates.add(builder.equal(x, data));
        return this;
    }

    public SpecificationWrapper<T> like(String name, String data) {
        return handle(name, e -> this.like(e, data));
    }

    public SpecificationWrapper<T> like(Expression<String> path, String data){
        predicates.add(builder.like(path, data));
        return this;
    }

    public SpecificationWrapper<T> startingWith(String name, String data) {
        this.like(name,data+"%");
        return this;
    }

    public SpecificationWrapper<T> endingWith(String name, String data) {
        this.like(name,"%"+data);
        return this;
    }

    public SpecificationWrapper<T> contains(String name, String data){
        this.like(name,"%"+data+"%");
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> gtOrEq(String name, Y data) {
        return handle(name, e -> this.gtOrEq(e, data));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> ltOrEq(String name, Y data) {
        return handle(name, e -> this.ltOrEq(e, data));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> gt(String name, Y data) {
        return handle(name, e -> this.gt(e, data));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> lt(String name, Y data) {
        return handle(name, e -> this.lt(e, data));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> gtOrEq
            (Expression<? extends Y> path, Y data) {
        predicates.add(builder.greaterThanOrEqualTo(path, data));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> ltOrEq
            (Expression<? extends Y> path, Y data) {
        predicates.add(builder.lessThanOrEqualTo(path, data));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> gt
            (Expression<? extends Y> path, Y data) {
        predicates.add(builder.greaterThan(path, data));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> lt
            (Expression<? extends Y> path, Y data) {
        predicates.add(builder.lessThan(path, data));
        return this;
    }

    public SpecificationWrapper<T> in(String name, Object... data) {
        return handle(name, e -> this.in(e, data));
    }

    public <U> SpecificationWrapper<T> in(Expression<? extends U> expression, Object... data) {
        predicates.add(expression.in(data));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> between(String name, Y start, Y end){
        predicates.add(builder.between(root.get(name), start, end));
        return this;
    }

    public SpecificationWrapper<T> in(String name, Collection<?> data) {
        return this.in(name, data.toArray());
    }

    public <U> Join<T,U> leftJoin(String fieldName) {
        return root.join(fieldName, JoinType.LEFT);
    }

    private SpecificationWrapper<T> handle(String name, Consumer<Path> action) {
        Path<?> path;
        if(name.contains(SEPARATOR)){
            String[] arr = name.split("\\"+SEPARATOR);
            path = this.leftJoin(arr[0]).get(arr[1]);
        }else{
            path = this.root.get(name);
        }
        action.accept(path);
        return this;
    }

}
