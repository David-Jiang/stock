package com.stock.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import com.stock.vo.StockVO;

@Component
public class MongoDBDao {
	
	@Resource
    private MongoTemplate mongoTemplate;
	
	public List<StockVO> getAllStockInfo() {
		return mongoTemplate.findAll(StockVO.class);
	}
	
	public void updateStockInfo(List<StockVO> stockInfoList) {
		mongoTemplate.insertAll(stockInfoList);
	}
}
