package com.springboot.datasource;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;

@Configuration
@MapperScan(basePackages = MysqlDatasourceConfig.PACKAGE)
public class MysqlDatasourceConfig {

	// mysqldao扫描路径
	static final String PACKAGE = "com.springboot.mysqldao";
	// mybatis mapper扫描路径
	static final String MAPPER_LOCATION = "classpath:mapper/mysql/*.xml";
    //mybatis 全局配置
    static final String MAPPER_CONFIG = "mybatis-config.xml";
    //扫描实体类
	static final String entityPackage = "com.springboot.entity";

	@Primary
	@Bean(name = "mysqldatasource")
	@ConfigurationProperties("spring.datasource.druid.mysql")
	public DataSource mysqlDataSource() {
		return DruidDataSourceBuilder.create().build();
	}

	@Bean
	@Primary
	public DataSourceTransactionManager mysqlTransactionManager() {
		return new DataSourceTransactionManager(mysqlDataSource());
	}

	@Bean
	@Primary
	public SqlSessionFactory mysqlSqlSessionFactory(@Qualifier("mysqldatasource") DataSource dataSource)
			throws Exception {
		final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		//配置mybatis全局
		sessionFactory.setConfigLocation(new ClassPathResource(MysqlDatasourceConfig.MAPPER_CONFIG));
		//扫描类
		sessionFactory.setTypeAliasesPackage(MysqlDatasourceConfig.entityPackage);
		//如果不使用xml的方式配置mapper，则可以省去下面这行mapper location的配置。
		sessionFactory.setMapperLocations(
				new PathMatchingResourcePatternResolver().getResources(MysqlDatasourceConfig.MAPPER_LOCATION));
		return sessionFactory.getObject();
	}
}
