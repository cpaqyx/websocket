package fastwave.cloud.stomp.security.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis相关配置
 */
@Configuration
@MapperScan({"fastwave.cloud.stomp.security.dao"})
public class MyBatisConfig {
}
