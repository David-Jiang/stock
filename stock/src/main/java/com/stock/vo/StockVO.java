package com.stock.vo;

import java.util.List;

import org.springframework.data.annotation.Id;

public class StockVO {
	@Id
	private String stockId;
	private String stockName;
	private List<SecuritiesVO> securitiesTradeList;
	private List<HistoryVO> historyPriceList;
	
	public String getStockId() {
		return stockId;
	}
	public void setStockId(String stockId) {
		this.stockId = stockId;
	}
	public String getStockName() {
		return stockName;
	}
	public void setStockName(String stockName) {
		this.stockName = stockName;
	}
	public List<SecuritiesVO> getSecuritiesTradeList() {
		return securitiesTradeList;
	}
	public void setSecuritiesTradeList(List<SecuritiesVO> securitiesTradeList) {
		this.securitiesTradeList = securitiesTradeList;
	}
	public List<HistoryVO> getHistoryPriceList() {
		return historyPriceList;
	}
	public void setHistoryPriceList(List<HistoryVO> historyPriceList) {
		this.historyPriceList = historyPriceList;
	}
}
