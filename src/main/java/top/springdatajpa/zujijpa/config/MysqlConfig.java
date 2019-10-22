package top.springdatajpa.zujijpa.config;

import org.hibernate.dialect.MySQL5Dialect;

/**
 * Mysql自动建表配置类
 * @author azheng
 * @since 2019/10/22
 */
public class MysqlConfig extends MySQL5Dialect {
    @Override
    public String getTableTypeString() {
        return " ENGINE=InnoDB DEFAULT CHARSET=utf8mb4";
    }
}
