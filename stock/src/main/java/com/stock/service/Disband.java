package com.stock.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.dao.CrmsmoeaDao;

@Component
public class Disband {
	
	@Autowired
    private CrmsmoeaDao crmsmoeaDao;
	
	public void updateDisband() throws Exception {
		try {
			crmsmoeaDao.doDisband();
		} catch (Exception e) {
			System.out.print(e.getMessage());
			throw e;
		}
	}
}
