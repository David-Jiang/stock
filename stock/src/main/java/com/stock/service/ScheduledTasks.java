package com.stock.service;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
	
	@Scheduled(cron = "0 0/10 * * * ?")
    public void keepAwake() {
		try {
			URL url = new URL("https://astrology-linebot.herokuapp.com/");
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
		    conn.connect();
		    
		    BufferedReader buffer = new BufferedReader(new InputStreamReader(conn.getInputStream(), "MS950"));
		    String line = "";
		    while((line = buffer.readLine()) != null) {
		    	line += "";
		    }
		    buffer.close();
			conn.disconnect();
		} catch (Exception e) {
			System.out.println("keepAwake發生錯誤，原因為：" + e.getMessage());
		}
    }
	
}
