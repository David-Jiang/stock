package com.stock.ctrl;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.stock.service.LocaleMessageSourceService;
import com.stock.service.OpenData;
import com.stock.vo.ErrorVO;
import com.stock.vo.StockVO;

@RestController
public class Controller {
	
	@Resource
	private LocaleMessageSourceService localeMessageService;
	
	@Autowired
	private ApplicationContext context;
	
	private Gson gson = new GsonBuilder().create();
	private ErrorVO errorVO = new ErrorVO();
	
	@RequestMapping(value = "/getStockInfo", produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String getStockInfo(final HttpServletResponse response) {
		OpenData openData = context.getBean(OpenData.class);
		List<StockVO> stockInfoList = null;
		try {
			stockInfoList = openData.getStockInfo();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			errorVO.setErrorMsg("發生錯誤，原因為：" + e.getMessage());
			return gson.toJson(errorVO);
		}
		return gson.toJson(stockInfoList);
	}
	
}
