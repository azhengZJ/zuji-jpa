package top.springdatajpa.zujijpa.utils;

import com.alibaba.fastjson.JSON;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Attribute Transfer Tool
 *
 * @author azheng
 * @since 2019/10/10
 */
@Slf4j
@UtilityClass
public class EntityUtils {

    /**
     *  集合转换成指定类型的List
     * @param source source
     * @param clazz clazz
     * @param <T> T
     * @return List
     */
    public static <T> List<T> cast(Collection<?> source, Class<T> clazz) {
        return JSON.parseArray(JSON.toJSONString(source), clazz);
    }

    public static Map<String, Object> castToMap(Object object) {
        try {
            Map<String, Object> map = new HashMap();
            for(Field field : object.getClass().getDeclaredFields()){
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(object);
                map.put(field.getName(), o);
                field.setAccessible(flag);
            }
            return map;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static Map<Field, Object> notNullCastToMap(Object object) {
        try {
            Map<Field, Object> map = new HashMap();
            for(Field field : object.getClass().getDeclaredFields()){
                boolean flag = field.isAccessible();
                field.setAccessible(true);
                Object o = field.get(object);
                if(o != null){
                    if(o instanceof String && !StringUtils.hasText((String)o)){
                        continue;
                    }
                    map.put(field, o);
                }
                field.setAccessible(flag);
            }
            return map;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
