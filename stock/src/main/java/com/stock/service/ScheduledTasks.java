package com.stock.service;


import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	@Scheduled(cron = "0 10 * ? * MON-FRI")
    public void keepAwakeFromHeroku() {
		try {
			System.out.println("keep awake by " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		} catch (Exception e) {
			System.out.println("排程發生錯誤，原因為：" + e.getMessage());
		}
    }
	
}
