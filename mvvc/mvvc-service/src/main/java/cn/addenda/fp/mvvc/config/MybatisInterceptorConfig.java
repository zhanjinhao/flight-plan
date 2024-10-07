package cn.addenda.fp.mvvc.config;

import cn.addenda.component.basemybatis.helper.MybatisBatchDmlHelper;
import cn.addenda.component.idgenerator.idgenerator.snowflake.DbWorkerIdGenerator;
import cn.addenda.component.idgenerator.idgenerator.snowflake.SnowflakeIdGenerator;
import cn.addenda.component.idgenerator.interceptor.IdFillingInterceptor;
import com.github.pagehelper.PageInterceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

/**
 * @author addenda
 * @since 2023/8/7 19:01
 */
@Configuration
public class MybatisInterceptorConfig implements EnvironmentAware {

  private Environment environment;

  @Bean
  public PageInterceptor pageInterceptor() {
    return new PageInterceptor();
  }

  @Bean
  public IdFillingInterceptor idFillingInterceptor(SnowflakeIdGenerator snowflakeIdGenerator) {
    return new IdFillingInterceptor(snowflakeIdGenerator);
  }

  @Bean
  public DbWorkerIdGenerator dbWorkerIdGenerator(DataSource dataSource) {
    return new DbWorkerIdGenerator(environment.resolvePlaceholders("${spring.application.name}"), dataSource);
  }

  @Bean
  public SnowflakeIdGenerator snowflakeIdGenerator(DbWorkerIdGenerator dbWorkerIdGenerator) {
    return new SnowflakeIdGenerator(dbWorkerIdGenerator);
  }

  @Bean
  public MybatisBatchDmlHelper mybatisBatchDmlHelper(SqlSessionFactory sqlSessionFactory) {
    return new MybatisBatchDmlHelper(sqlSessionFactory);
  }

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }
}
