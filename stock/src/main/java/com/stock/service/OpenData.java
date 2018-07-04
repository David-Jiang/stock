package com.stock.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.dao.MongoDBDao;
import com.stock.util.SslUtil;
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
	
	public void updateStockInfo(String parameter) throws Exception {
		SslUtil.ignoreSsl();
		String urlPath = "https://stock.wearn.com/netbuy.asp?kind=";
		HttpsURLConnection conn = null;
		BufferedReader buffer = null;
		List<String> stockIdArr = new ArrayList<>();
		stockIdArr.add(parameter);
		List<StockVO> stockInfoList = new ArrayList<>();
		try {
			for (String stockId : stockIdArr) {
				StockVO stockVO = new StockVO();
				List<SecuritiesVO> securitiesTradeList = new ArrayList<>();
				
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
				    		tempArr[3] = tempArr[3].split("</table>")[0];
				    	}
				    	SecuritiesVO securitiesVO = new SecuritiesVO();
				    	securitiesVO.setTransactionDate(tempArr[0].trim());
				    	securitiesVO.setInvestAmount(Integer.parseInt(tempArr[1].trim().replaceAll(",", "")));
				    	securitiesVO.setNativeAmount(Integer.parseInt(tempArr[2].trim().replaceAll(",", "")));
				    	securitiesVO.setForeignAmount(Integer.parseInt(tempArr[3].trim().replaceAll(",", "")));
				    	securitiesVO.setTotalAmount(securitiesVO.getInvestAmount() + securitiesVO.getNativeAmount() + securitiesVO.getForeignAmount());
				    	securitiesTradeList.add(securitiesVO);
			    	} else {
			    		String stockName = sourceArr[0].split("<font size=\"3\">")[1].split("</font>")[0].replaceAll(stockId, "").trim();
			    		stockVO.setStockId(stockId);
			    		stockVO.setStockName(stockName);
			    	}
			    	count++;
			    }
			    stockVO.setSecuritiesTradeList(securitiesTradeList);
			    stockInfoList.add(stockVO);
			    buffer.close();
				conn.disconnect();
				Thread.sleep(5000);
			}
			mongoDBDao.updateStockInfo(stockInfoList);
		} catch (Exception e) {
			buffer.close();
			conn.disconnect();
			throw e;
		}
	}
}
