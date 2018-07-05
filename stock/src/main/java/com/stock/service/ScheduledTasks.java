package com.stock.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
	
	@Autowired
	private ApplicationContext context;
	
	@Scheduled(cron = "0 0 18 ? * MON-FRI")
    public void stockInfoTask() {
		OpenData openData = context.getBean(OpenData.class);
		try {
			openData.updateStockInfo();
		} catch (Exception e) {
			System.out.println("排程發生錯誤，原因為：" + e.getMessage());
		}
    }
	
}
