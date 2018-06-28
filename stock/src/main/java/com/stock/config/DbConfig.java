package com.stock.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource(value = "classpath:ds.properties", ignoreResourceNotFound = true)
public class DbConfig {
	@Autowired
	private Environment env;
	
	@Bean(name = "ds_pubfts")
	@Primary
	public DataSource dataSource() {
		DataSourceBuilder d = DataSourceBuilder.create();
		d.driverClassName(env.getProperty("driver"));
		d.url(env.getProperty("url"));
		d.username(env.getProperty("pubfts.username"));
		d.password(env.getProperty("pubfts.password"));
		return d.build();
	}
	
	@Bean(name = "ds_crmsmoea")
	public DataSource dataSource1() {
		DataSourceBuilder d = DataSourceBuilder.create();
		d.driverClassName(env.getProperty("driver"));
		d.url(env.getProperty("url"));
		d.username(env.getProperty("crmsmoea.username"));
		d.password(env.getProperty("crmsmoea.password"));
		return d.build();
	}
	
	@Bean(name = "jdbc_pubfts")
    public JdbcTemplate jdbcTemplate(@Qualifier("ds_pubfts") DataSource dataSource) { 
        return new JdbcTemplate(dataSource); 
    }
	
	@Bean(name = "jdbc_crmsmoea")
    public JdbcTemplate jdbcTemplate1(@Qualifier("ds_crmsmoea") DataSource dataSource) { 
        return new JdbcTemplate(dataSource); 
    }
	
	@Bean(name = "tm_pubfts")
    public PlatformTransactionManager testTransactionManager(@Qualifier("ds_pubfts") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }
	
	@Bean(name = "tm_crmsmoea")
    public PlatformTransactionManager testTransactionManager1(@Qualifier("ds_crmsmoea") DataSource dataSource) {
        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        return dataSourceTransactionManager;
    }
	
	@Bean(name = "jdbc_icbs")
    public JdbcTemplate jdbcTemplate2() { 
		DataSourceBuilder d = DataSourceBuilder.create();
		d.driverClassName(env.getProperty("driver"));
		d.url(env.getProperty("url"));
		d.username(env.getProperty("icbs.username"));
		d.password(env.getProperty("icbs.password"));
        return new JdbcTemplate(d.build()); 
    }
	
	@Bean(name = "jdbc_ctmg") 
    public JdbcTemplate jdbcTemplate3() { 
		DataSourceBuilder d = DataSourceBuilder.create();
		d.driverClassName(env.getProperty("mysql.driver"));
		d.url(env.getProperty("ctmg_jdbc.url"));
		d.username(env.getProperty("ctmg_jdbc.username"));
		d.password(env.getProperty("ctmg_jdbc.password"));
        return new JdbcTemplate(d.build()); 
    }
	
	@Bean(name = "jdbc_ctmgreg") 
    public JdbcTemplate jdbcTemplate4() { 
		DataSourceBuilder d = DataSourceBuilder.create();
		d.driverClassName(env.getProperty("mysql.driver"));
		d.url(env.getProperty("ctmgreg_jdbc.url"));
		d.username(env.getProperty("ctmgreg_jdbc.username"));
		d.password(env.getProperty("ctmgreg_jdbc.password"));
        return new JdbcTemplate(d.build()); 
    }
	
	@Bean(name = "jdbc_ctmgoa") 
    public JdbcTemplate jdbcTemplate5() { 
		DataSourceBuilder d = DataSourceBuilder.create();
		d.driverClassName(env.getProperty("mysql.driver"));
		d.url(env.getProperty("ctmgoa_jdbc.url"));
		d.username(env.getProperty("ctmgoa_jdbc.username"));
		d.password(env.getProperty("ctmgoa_jdbc.password"));
        return new JdbcTemplate(d.build()); 
    }
}