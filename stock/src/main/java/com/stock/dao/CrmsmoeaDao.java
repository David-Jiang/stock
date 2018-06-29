package com.stock.dao;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(value = "tm_crmsmoea",rollbackFor = Exception.class)
public class CrmsmoeaDao {
	
//	@Autowired
//	@Qualifier("jdbc_crmsmoea")
//	private JdbcTemplate jdbcTemplate;
//
//	public String test() {
//		return "";
//	}
}
