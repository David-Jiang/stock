package com.stock.ctrl;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.stock.service.Disband;
import com.stock.service.LocaleMessageSourceService;
import com.stock.service.OpenData;
import com.stock.service.Report;

@RestController
public class Controller {
	
	@Resource
	private LocaleMessageSourceService localeMessageService;
	
	@Autowired
	private ApplicationContext context;
	
	private Gson gson = new Gson();
	
	@RequestMapping(value = "/test")
	public String test(final HttpServletResponse response) {
		Disband disband = context.getBean(Disband.class);
		try {
			disband.updateDisband();
		} catch (Exception e) {
			return gson.toJson("發生error : " + e.getMessage());
		}
		return gson.toJson("success");
	}
	
	@RequestMapping(value = "/stock")
	public String stock(final HttpServletResponse response) {
		OpenData openData = context.getBean(OpenData.class);
		try {
			openData.updateStockDetail();
		} catch (Exception e) {
			return gson.toJson("發生error : " + e.getMessage());
		}
		return gson.toJson("success");
	}
	
	@RequestMapping(value = "/trade")
	public String trade(final HttpServletResponse response) {
		OpenData openData = context.getBean(OpenData.class);
		String answer = null;
		try {
			answer = openData.updateTradeDetail();
		} catch (Exception e) {
			return gson.toJson("發生error : " + e.getMessage());
		}
		return gson.toJson(answer);
	}
	
	@RequestMapping(value = "/rpt1")
	public void rpt1(final HttpServletResponse response) throws Exception {
		Report report = context.getBean(Report.class);
		report.fts(response);
	}
	
	@RequestMapping(value = "/rpt2")
	public void rpt2(final HttpServletResponse response) throws Exception {
		Report report = context.getBean(Report.class);
		report.ctmg(response);
	}
	
	@RequestMapping(value = "/rpt3")
	public void rpt3(final HttpServletResponse response) throws Exception {
		Report report = context.getBean(Report.class);
		report.ctmgoa(response);
	}
	
	@RequestMapping(value = "/rpt4")
	public void rpt4(final HttpServletResponse response) throws Exception {
		Report report = context.getBean(Report.class);
		report.ctmgrpt(response);
	}
	
	@RequestMapping(value = "/say")
	public void say(final HttpServletResponse response,@RequestParam("startDate") String startDate,
			@RequestParam("endDate") String endDate) throws Exception {
		Report report = context.getBean(Report.class);
		report.say(response,startDate,endDate);
	}
	
}
