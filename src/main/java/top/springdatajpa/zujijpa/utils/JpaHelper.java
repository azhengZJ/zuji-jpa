package top.springdatajpa.zujijpa.utils;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

/**
 * <p>
 * JPA Helper
 * </p>
 *
 * @author azheng
 * @since 2019-10-10
 */
@UtilityClass
public class JpaHelper {
    public <T> Page<T> castPage(Page<?> pageList, PageRequest page, Class<T> toClass){
        List<T> result = EntityUtils.cast(pageList.getContent(), toClass);
        return new PageImpl<T>(result, page, pageList.getTotalElements());
    }
    public <T> Page<T> castPage(Page<?> pageList, Class<T> toClass){
        PageRequest page = PageRequest.of(pageList.getNumber(), pageList.getSize());
        List<T> result = EntityUtils.cast(pageList.getContent(), toClass);
        return new PageImpl<T>(result, page, pageList.getTotalElements());
    }
}
