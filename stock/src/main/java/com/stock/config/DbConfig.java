package com.stock.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:ds.properties", ignoreResourceNotFound = true)
public class DbConfig {
//	@Autowired
//	private Environment env;
	
//	@Bean(name = "ds_pubfts")
//	@Primary
//	public DataSource dataSource() {
//		DataSourceBuilder d = DataSourceBuilder.create();
//		d.driverClassName(env.getProperty("driver"));
//		d.url(env.getProperty("url"));
//		d.username(env.getProperty("username"));
//		d.password(env.getProperty("password"));
//		return d.build();
//	}
//	
//	@Bean(name = "jdbc_pubfts")
//    public JdbcTemplate jdbcTemplate(@Qualifier("ds_pubfts") DataSource dataSource) { 
//        return new JdbcTemplate(dataSource); 
//    }
//	
//	@Bean(name = "tm_pubfts")
//    public PlatformTransactionManager testTransactionManager(@Qualifier("ds_pubfts") DataSource dataSource) {
//        DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
//        return dataSourceTransactionManager;
//    }
}