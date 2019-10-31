package top.springdatajpa.zujijpa.wrapper;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@NoArgsConstructor
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

    public SpecificationWrapper<T> newWrapper(boolean isConjunction, Consumer<SpecificationWrapper<T>> action) {
        SpecificationWrapper<T> specification = new SpecificationWrapper<>(root, query, builder);
        CriteriaBuilder newBuilder = specification.getBuilder();
        Predicate predicate = isConjunction ? newBuilder.conjunction() : newBuilder.disjunction();
        action.accept(specification);
        predicate.getExpressions().addAll(specification.getPredicates()) ;
        predicates.add(predicate);
        return this;
    }

    public SpecificationWrapper<T> isNull(boolean condition, String name) {
        return condition ? this.isNull(name) : this;
    }

    public SpecificationWrapper<T> isNull(String name) {
        return handle(name, e -> this.isNull(e));
    }

    public SpecificationWrapper<T> isNull(Expression<?> x) {
        predicates.add(builder.isNull(x));
        return this;
    }

    public SpecificationWrapper<T> isNotNull(boolean condition, String name) {
        return condition ? this.isNotNull(name) : this;
    }

    public SpecificationWrapper<T> isNotNull(String name) {
        return handle(name, e -> this.isNotNull(e));
    }

    public SpecificationWrapper<T> isNotNull(Expression<?> x) {
        predicates.add(builder.isNotNull(x));
        return this;
    }

    public SpecificationWrapper<T> eq(boolean condition, String name, Object value) {
        return condition ? this.eq(name, value) : this;
    }

    public SpecificationWrapper<T> eq(String name, Object value) {
        return handle(name, e -> this.eq(e, value));
    }

    public SpecificationWrapper<T> eq(Expression<?> x, Object value) {
        predicates.add(builder.equal(x, value));
        return this;
    }

    public SpecificationWrapper<T> ne(boolean condition, String name, Object value) {
        return condition ? this.ne(name, value) : this;
    }

    public SpecificationWrapper<T> ne(String name, Object value) {
        return handle(name, e -> this.ne(e, value));
    }

    public SpecificationWrapper<T> ne(Expression<?> x, Object value) {
        predicates.add(builder.notEqual(x, value));
        return this;
    }

    public SpecificationWrapper<T> like(boolean condition, String name, String value) {
        return condition ? this.like(name, value) : this;
    }

    public SpecificationWrapper<T> like(String name, String value) {
        return handle(name, e -> this.like(e, value));
    }

    public SpecificationWrapper<T> like(Expression<String> path, String value){
        predicates.add(builder.like(path, value));
        return this;
    }

    public SpecificationWrapper<T> startingWith(boolean condition, String name, String value) {
        return condition ? this.startingWith(name, value) : this;
    }

    public SpecificationWrapper<T> startingWith(String name, String value) {
        this.like(name,value+"%");
        return this;
    }

    public SpecificationWrapper<T> endingWith(boolean condition, String name, String value) {
        return condition ? this.endingWith(name, value) : this;
    }

    public SpecificationWrapper<T> endingWith(String name, String value) {
        this.like(name,"%"+value);
        return this;
    }

    public SpecificationWrapper<T> contains(boolean condition, String name, String value) {
        return condition ? this.contains(name, value) : this;
    }

    public SpecificationWrapper<T> contains(String name, String value){
        this.like(name,"%"+value+"%");
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> ge(boolean condition, String name, Y value) {
        return condition ? this.ge(name, value) : this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> ge(String name, Y value) {
        return handle(name, e -> this.ge(e, value));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> ge
            (Expression<? extends Y> path, Y value) {
        predicates.add(builder.greaterThanOrEqualTo(path, value));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> le(boolean condition, String name, Y value) {
        return condition ? this.le(name, value) : this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> le(String name, Y value) {
        return handle(name, e -> this.le(e, value));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> le
            (Expression<? extends Y> path, Y value) {
        predicates.add(builder.lessThanOrEqualTo(path, value));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> gt(boolean condition, String name, Y value) {
        return condition ? this.gt(name, value) : this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> gt(String name, Y value) {
        return handle(name, e -> this.gt(e, value));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> gt
            (Expression<? extends Y> path, Y value) {
        predicates.add(builder.greaterThan(path, value));
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> lt(boolean condition, String name, Y value) {
        return condition ? this.lt(name, value) : this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> lt(String name, Y value) {
        return handle(name, e -> this.lt(e, value));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> lt
            (Expression<? extends Y> path, Y value) {
        predicates.add(builder.lessThan(path, value));
        return this;
    }

    public SpecificationWrapper<T> in(boolean condition, String name, Object... value) {
        return condition ? in(name, value) : this;
    }

    public SpecificationWrapper<T> in(String name, Object... value) {
        return handle(name, e -> this.in(e, value));
    }

    public SpecificationWrapper<T> in(boolean condition, String name, Collection<?> value) {
        return condition ? in(name, value) : this;
    }

    public SpecificationWrapper<T> in(String name, Collection<?> value) {
        return this.in(name, value.toArray());
    }

    public <U> SpecificationWrapper<T> in(Expression<? extends U> expression, Object... value) {
        predicates.add(expression.in(value));
        return this;
    }

    public SpecificationWrapper<T> notIn(boolean condition, String name, Collection<?> value) {
        return condition ? notIn(name, value) : this;
    }

    public SpecificationWrapper<T> notIn(String name, Collection<?> value) {
        return handle(name, e -> this.notIn(e, value.toArray()));
    }

    public SpecificationWrapper<T> notIn(boolean condition, String name, Object... value) {
        return condition ? notIn(name, value) : this;
    }

    public SpecificationWrapper<T> notIn(String name, Object... value) {
        return handle(name, e -> this.notIn(e, value));
    }

    public <U> SpecificationWrapper<T> notIn(Expression<? extends U> expression, Object... value) {
        predicates.add(expression.in(value).not());
        return this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> between(boolean condition,
                                                                             String name, Y start, Y end){
        return condition ? between(name, start, end) : this;
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> between(String name, Y start, Y end){
        return handle(name, e -> this.between(e, start, end));
    }

    public <Y extends Comparable<? super Y>> SpecificationWrapper<T> between(Expression<? extends Y> path,
                                                                             Y start, Y end){
        predicates.add(builder.between(path, start, end));
        return this;
    }

    public <U> Join<T,U> leftJoin(String fieldName) {
        return root.join(fieldName, JoinType.LEFT);
    }

    public SpecificationWrapper<T> handle(String name, Consumer<Path> action) {
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
