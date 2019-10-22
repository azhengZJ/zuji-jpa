package top.springdatajpa.zujijpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author azheng
 * @since 2019/10/22
 * @param <T>
 */
@NoRepositoryBean
public interface BaseRepository<T>  extends JpaRepository<T,Long>, JpaSpecificationExecutor<T> {
}