package top.springdatajpa.zujijpa;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;
import top.springdatajpa.zujijpa.annotation.QueryIgnore;
import top.springdatajpa.zujijpa.annotation.QueryOperator;
import top.springdatajpa.zujijpa.enums.Operator;
import top.springdatajpa.zujijpa.utils.EntityUtils;
import top.springdatajpa.zujijpa.wrapper.OperatorWrapper;
import top.springdatajpa.zujijpa.wrapper.SpecificationWrapper;

import javax.persistence.JoinColumn;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Specifications query the core tool class
 * @author azheng
 * @since 2019/5/7
 */
public class Specifications {

    public static <T> Specification<T> where(Consumer<SpecificationWrapper<T>> action) {
        return where(true, action);
    }

    /**
     *  Specification generate the most core method
     * @param isConjunction True is `and` connection, false is `or` connection
     * @param action Query code
     * @param <T>  Main table entity class of query
     * @return Specification
     */
    public static <T> Specification<T> where(boolean isConjunction, Consumer<SpecificationWrapper<T>> action) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            SpecificationWrapper<T> specification = new SpecificationWrapper<>(root, query, builder);
            action.accept(specification);
            List<Predicate> predicates = specification.getPredicates();
            Predicate[] arr = predicates.toArray(new Predicate[predicates.size()]);
            return isConjunction?builder.and(arr):builder.or(arr);
        };
    }

    public static <T> Specification<T> conditionOf(Object object) {
        return conditionOf(object, e -> {});
    }

    public static <T> Specification<T> conditionOf(Object object, Consumer<SpecificationWrapper<T>> action) {
        Map<Field,Object> map = EntityUtils.notNullCastToMap(object);
        Specification<T> s =  where(e -> {
            map.forEach((k, v) -> {
                OperatorWrapper wrapper = new OperatorWrapper();
                wrapper.setSpecWrapper(e);
                wrapper.setValue(v);
                QueryIgnore ignore = k.getAnnotation(QueryIgnore.class);
                QueryOperator query = k.getAnnotation(QueryOperator.class);
                JoinColumn joinCol = k.getAnnotation(JoinColumn.class);
                if(ignore != null) return;
                Operator operator = query != null?query.value(): Operator.EQ;
                if(v instanceof Collection){
                    operator = Operator.IN;
                }
                if(query != null && StringUtils.hasText(query.fieldName())){
                    wrapper.setName(getPathName(query.fieldName(), joinCol));
                }else{
                    wrapper.setName(getPathName(k.getName(), joinCol));
                }
                operator.consumer().accept(wrapper);
            });
            action.accept(e);
        });
        return s;
    }

    public static <T> Specification<T> extendWhere(boolean isConjunction, SpecificationWrapper specification, Consumer<SpecificationWrapper> action) {
        return (Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) -> {
            specification.setRoot(root);
            specification.setQuery(query);
            specification.setBuilder(builder);
            action.accept(specification);
            List<Predicate> predicates = specification.getPredicates();
            Predicate[] arr = predicates.toArray(new Predicate[predicates.size()]);
            return isConjunction?builder.and(arr):builder.or(arr);
        };
    }

    public static <T> Specification<T> extendWhere(SpecificationWrapper specification, Consumer<? extends SpecificationWrapper> action) {
        return extendWhere(true, specification, (Consumer<SpecificationWrapper>) action);
    }

    public static <T> Specification<T> extendConditionOf(Boolean isConjunction, Object object, SpecificationWrapper specification,
                                                         Consumer<SpecificationWrapper> action) {
        Map<Field,Object> map = EntityUtils.notNullCastToMap(object);
        Specification<T> s =  extendWhere(isConjunction, specification, e -> {
            map.forEach((k, v) -> {
                OperatorWrapper wrapper = new OperatorWrapper();
                wrapper.setSpecWrapper(e);
                wrapper.setValue(v);
                QueryIgnore ignore = k.getAnnotation(QueryIgnore.class);
                QueryOperator query = k.getAnnotation(QueryOperator.class);
                JoinColumn joinCol = k.getAnnotation(JoinColumn.class);
                if(ignore != null) return;
                Operator operator = query != null?query.value(): Operator.EQ;
                if(v instanceof Collection){
                    operator = Operator.IN;
                }
                if(query != null && StringUtils.hasText(query.fieldName())){
                    wrapper.setName(getPathName(query.fieldName(), joinCol));
                }else{
                    wrapper.setName(getPathName(k.getName(), joinCol));
                }
                operator.consumer().accept(wrapper);
            });
            action.accept(e);
        });
        return s;
    }

    public static <T> Specification<T> extendConditionOf(Object object, SpecificationWrapper specification,
                                                         Consumer<? extends SpecificationWrapper> action) {
        return extendConditionOf(true, object, specification, (Consumer<SpecificationWrapper>) action);
    }

    private static String getPathName(String name, JoinColumn joinCol){
        if(joinCol != null && !name.contains(".")){
            name = joinCol.name() + "." + name;
        }
        return name;
    }

}
