package cn.addenda.fp.mvvc.config;

import cn.addenda.component.jdk.util.collection.ArrayUtils;
import cn.addenda.sql.vitamin.client.mybatis.helper.MsIdExtractHelper;
import cn.addenda.sql.vitamin.client.mybatis.helper.PageHelperMsIdExtractHelper;
import cn.addenda.sql.vitamin.client.mybatis.interceptor.baseentity.MyBatisBaseEntityInterceptor;
import cn.addenda.sql.vitamin.client.mybatis.interceptor.tombstone.MyBatisTombstoneInterceptor;
import cn.addenda.sql.vitamin.client.spring.aop.baseentity.BaseEntityRewriterConfigurer;
import cn.addenda.sql.vitamin.client.spring.aop.baseentity.EnableBaseEntity;
import cn.addenda.sql.vitamin.client.spring.aop.tombstone.EnableTombstone;
import cn.addenda.sql.vitamin.client.spring.aop.tombstone.TombstoneRewriterConfigurer;
import cn.addenda.sql.vitamin.rewriter.baseentity.DefaultBaseEntitySource;
import cn.addenda.sql.vitamin.rewriter.baseentity.DruidBaseEntityRewriter;
import cn.addenda.sql.vitamin.rewriter.convertor.DefaultDataConvertorRegistry;
import cn.addenda.sql.vitamin.rewriter.tombstone.DruidTombstoneRewriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author addenda
 * @since 2023/8/7 19:16
 */
@EnableTombstone(order = 4)
@EnableBaseEntity(order = 3)
@Configuration
public class SqlVitaminConfig {

  @Bean
  public MsIdExtractHelper msIdExtractHelper() {
    return new PageHelperMsIdExtractHelper();
  }

  @Bean
  public MyBatisBaseEntityInterceptor myBatisBaseEntityInterceptor(MsIdExtractHelper msIdExtractHelper) {
    return new MyBatisBaseEntityInterceptor(msIdExtractHelper);
  }

  @Bean
  public MyBatisTombstoneInterceptor myBatisTombstoneInterceptor(MsIdExtractHelper msIdExtractHelper) {
    return new MyBatisTombstoneInterceptor(msIdExtractHelper);
  }

  @Bean
  public TombstoneRewriterConfigurer tombstoneRewriterConfigurer() {
    return new TombstoneRewriterConfigurer(
            new DruidTombstoneRewriter(
                    null,
                    ArrayUtils.asArrayList("t_snow_flake_worker_id"),
                    new DefaultDataConvertorRegistry()));
  }

  @Bean
  public BaseEntityRewriterConfigurer baseEntityRewriterConfigurer() {
    return new BaseEntityRewriterConfigurer(
            new DruidBaseEntityRewriter(
                    null,
                    ArrayUtils.asArrayList("t_snow_flake_worker_id"),
                    new DefaultBaseEntitySource(),
                    new DefaultDataConvertorRegistry()));
  }

}
