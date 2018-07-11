package com.stock.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.stereotype.Component;

import com.stock.config.Application;
import com.stock.dao.MongoDBDao;
import com.stock.util.DateUtil;
import com.stock.util.SslUtil;
import com.stock.vo.HistoryVO;
import com.stock.vo.SecuritiesVO;
import com.stock.vo.StockVO;

@Component
public class OpenData {
	
	@Autowired
	MongoDBDao mongoDBDao;
	
	public List<StockVO> getStockInfo() throws Exception {
		List<StockVO> stockInfoList = null;
		try {
			stockInfoList = mongoDBDao.getAllStockInfo();
		} catch (Exception e) {
			throw e;
		}
		return stockInfoList;
	}
	
	public void insertStockInfo(String parameter) throws Exception {
		List<String> stockIdArr = new ArrayList<>();
		stockIdArr.add(parameter);
		
		SslUtil.ignoreSsl();
		String[] urlPathArr = {"https://stock.wearn.com/netbuy.asp?kind=", "https://stock.wearn.com/cdata.asp?kind="};
		HttpsURLConnection conn = null;
		BufferedReader buffer = null;
		
		List<StockVO> stockInfoList = new ArrayList<>();
		try {
			for (String stockId : stockIdArr) {
				StockVO stockVO = new StockVO();
				
				for (String urlPath: urlPathArr) {
					List<SecuritiesVO> securitiesTradeList = new ArrayList<>();
					List<HistoryVO> historyPriceList = new ArrayList<>();
					
					URL url = new URL(urlPath + stockId);
					conn = (HttpsURLConnection) url.openConnection();
				    conn.connect();
				    
				    String line = null;
				    StringBuffer str = new StringBuffer();
				    buffer = new BufferedReader(new InputStreamReader(conn.getInputStream(), "MS950"));
				    while((line = buffer.readLine()) != null) {
				    	str.append(line);
				    }
				    int count = 0;
				    String[] sourceArr = str.toString().split("<tr class=\"stockalllistbg");
				    for (String source : sourceArr) {
				    	if (count > 0) {
				    		String temp = source.trim().replaceAll("\"1\">", "");
				    		temp = temp.replaceAll("1\">", "");
					    	temp = temp.replaceAll("2\">", "");
					    	temp = temp.replaceAll("</tr>", "");
					    	temp = temp.replaceAll("</td>", "");
					    	temp = temp.replaceAll("&nbsp;", "");
					    	temp = temp.replaceAll("<td align=\"center\">", "");
					    	temp = temp.replaceAll("\\t", "");
					    	String[] tempArr = temp.split("<td align=\"right\">");
					    	if (count == sourceArr.length - 1) {
					    		if (urlPath.indexOf("netbuy") > -1) {
					    			tempArr[3] = tempArr[3].split("</table>")[0];
					    		} else {
					    			tempArr[5] = tempArr[5].split("</table>")[0];
					    		}
					    	}
					    	if (urlPath.indexOf("netbuy") > -1) {
					    		securitiesTradeList.add(changeToSecuritiesVO(tempArr));
					    	} else {
					    		historyPriceList.add(changeToHistoryVO(tempArr));
					    	}
				    	} else {
				    		if (StringUtils.isBlank(stockVO.getStockId())) {
				    			String stockName = sourceArr[0].split("<font size=\"3\">")[1].split("</font>")[0].replaceAll(stockId, "").trim();
					    		stockVO.setStockId(stockId);
					    		stockVO.setStockName(stockName);
				    		}
				    	}
				    	count++;
				    }
				    if (urlPath.indexOf("netbuy") > -1) {
				    	stockVO.setSecuritiesTradeList(securitiesTradeList);
			    	} else {
			    		stockVO.setHistoryPriceList(historyPriceList);
			    	}
				    buffer.close();
					conn.disconnect();
					Thread.sleep(5000);
				}
				
				stockInfoList.add(stockVO);
			}
			
			
			
			mongoDBDao.insertStockInfo(stockInfoList);
		} catch (Exception e) {
			buffer.close();
			conn.disconnect();
			throw e;
		}
	}
	
	public void updateStockInfo() throws Exception {
		List<StockVO> stockInfoList = mongoDBDao.getAllStockInfo();
		
		SslUtil.ignoreSsl();
		String[] urlPathArr = {"https://stock.wearn.com/netbuy.asp?kind=", "https://stock.wearn.com/cdata.asp?kind="};
		HttpsURLConnection conn = null;
		BufferedReader buffer = null;
		
		try {
			for (StockVO stockVO : stockInfoList) {
				String stockId = stockVO.getStockId();
				List<SecuritiesVO> securitiesTradeList = stockVO.getSecuritiesTradeList();
				List<HistoryVO> historyPriceList = stockVO.getHistoryPriceList();
				
				for (String urlPath : urlPathArr) {
					URL url = new URL(urlPath + stockId);
					conn = (HttpsURLConnection) url.openConnection();
				    conn.connect();
				    
				    String line = null;
				    StringBuffer str = new StringBuffer();
				    buffer = new BufferedReader(new InputStreamReader(conn.getInputStream(), "MS950"));
				    while((line = buffer.readLine()) != null) {
				    	str.append(line);
				    }
				    int count = 0;
				    String[] sourceArr = str.toString().split("<tr class=\"stockalllistbg");
				    for (String source : sourceArr) {
				    	if (count > 0) {
				    		String temp = source.trim().replaceAll("\"1\">", "");
				    		temp = temp.replaceAll("1\">", "");
					    	temp = temp.replaceAll("2\">", "");
					    	temp = temp.replaceAll("</tr>", "");
					    	temp = temp.replaceAll("</td>", "");
					    	temp = temp.replaceAll("&nbsp;", "");
					    	temp = temp.replaceAll("<td align=\"center\">", "");
					    	temp = temp.replaceAll("\\t", "");
					    	String[] tempArr = temp.split("<td align=\"right\">");
					    	if (count == sourceArr.length - 1) {
					    		if (urlPath.indexOf("netbuy") > -1) {
					    			tempArr[3] = tempArr[3].split("</table>")[0];
					    		} else {
					    			tempArr[5] = tempArr[5].split("</table>")[0];
					    		}
					    	}
					    	String transactionDate = tempArr[0].trim();
					    	if (urlPath.indexOf("netbuy") > -1) {
						    	if (securitiesTradeList.stream().noneMatch(vo -> StringUtils.equals(vo.getTransactionDate(), transactionDate))) {
						    		securitiesTradeList.add(changeToSecuritiesVO(tempArr));
						    	}
					    	} else {
						    	if (historyPriceList.stream().noneMatch(vo -> StringUtils.equals(vo.getTransactionDate(), transactionDate))) {
						    		historyPriceList.add(changeToHistoryVO(tempArr));
						    	}
					    	}
				    	}
				    	count++;
				    }
				}
				
			    Comparator<SecuritiesVO> comparatorSecurities = Comparator.comparing(SecuritiesVO::getTransactionDate); 
			    Collections.sort(securitiesTradeList, comparatorSecurities.reversed());
			    stockVO.setSecuritiesTradeList(securitiesTradeList);
			    Comparator<HistoryVO> comparatorHistory = Comparator.comparing(HistoryVO::getTransactionDate); 
			    Collections.sort(historyPriceList, comparatorHistory.reversed());
			    stockVO.setHistoryPriceList(historyPriceList);
			    
			    buffer.close();
				conn.disconnect();
				Thread.sleep(5000);
			}
			mongoDBDao.updateAllStockInfo(stockInfoList);
		} catch (Exception e) {
			buffer.close();
			conn.disconnect();
			throw e;
		}
	}
	
	private SecuritiesVO changeToSecuritiesVO(String[] source) {
		SecuritiesVO securitiesVO = new SecuritiesVO();
    	securitiesVO.setTransactionDate(source[0].trim());
    	securitiesVO.setInvestAmount(Integer.parseInt(source[1].trim().replaceAll(",", "")));
    	securitiesVO.setNativeAmount(Integer.parseInt(source[2].trim().replaceAll(",", "")));
    	securitiesVO.setForeignAmount(Integer.parseInt(source[3].trim().replaceAll(",", "")));
    	securitiesVO.setTotalAmount(securitiesVO.getInvestAmount() + securitiesVO.getNativeAmount() + securitiesVO.getForeignAmount());
    	return securitiesVO;
	}
	
	private HistoryVO changeToHistoryVO(String[] source) {
		HistoryVO historyVO = new HistoryVO();
		historyVO.setTransactionDate(source[0].trim());
		historyVO.setStartPrice(source[1].trim());
		historyVO.setHighPrice(source[2].trim());
		historyVO.setLowPrice(source[3].trim());
		historyVO.setEndPrice(source[4].trim());
		historyVO.setTransactionAmount(source[5].trim());
    	return historyVO;
	}
}
