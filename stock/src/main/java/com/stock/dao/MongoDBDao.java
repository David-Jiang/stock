package com.stock.dao;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import com.stock.vo.StockVO;

@Component
public class MongoDBDao {
	
	@Resource
    private MongoTemplate mongoTemplate;
	
	public List<StockVO> getAllStockInfo() {
		return mongoTemplate.findAll(StockVO.class);
	}
	
	public void insertStockInfo(List<StockVO> stockInfoList) {
		for (StockVO stockVO : stockInfoList) {
			if (!mongoTemplate.exists(new Query(Criteria.where("stockId").is(stockVO.getStockId())), StockVO.class)) {
				mongoTemplate.insert(stockVO);
			}
		}
	}
	
	public void updateAllStockInfo(List<StockVO> stockInfoList) {
		for (StockVO stockVO : stockInfoList) {
			mongoTemplate.updateFirst(new Query(Criteria.where("stockId").is(stockVO.getStockId())), 
					new Update().set("securitiesTradeList", stockVO.getSecuritiesTradeList()).set("historyPriceList", stockVO.getHistoryPriceList())
					, StockVO.class);
		}
	}
}
