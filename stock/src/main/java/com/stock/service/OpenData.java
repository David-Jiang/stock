package com.stock.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.dao.PubftsDao;
import com.stock.util.DateUtil;
import com.stock.util.SslUtil;
import com.stock.vo.StockVO;
import com.stock.vo.TradeVO;

import de.siegmar.fastcsv.reader.CsvContainer;
import de.siegmar.fastcsv.reader.CsvReader;
import de.siegmar.fastcsv.reader.CsvRow;

@Component
public class OpenData {
	
	@Autowired
    private PubftsDao ftsDao;
	
	private final Logger logger = LoggerFactory.getLogger("logs");

	public void updateStockDetail() throws Exception {
		List<StockVO> stockList = new ArrayList<StockVO>();
		String[] urlArr = {"http://dts.twse.com.tw/opendata/t187ap03_L.csv","http://dts.twse.com.tw/opendata/t187ap03_O.csv",
						   "http://dts.twse.com.tw/opendata/t187ap03_R.csv","http://dts.twse.com.tw/opendata/t187ap03_P.csv"};
		String dataSource = "金融監督管理委員會證券期貨局";
		String[] dataSourceUrlArr = {"https://data.gov.tw/dataset/18419","https://data.gov.tw/dataset/25036",
									 "https://data.gov.tw/dataset/28568","https://data.gov.tw/dataset/28567"};
		String[] stockTypeArr = {"2","3","5","1"};
		try {
			Map<String,String> stockNameMap = this.getStockNameList();
			for (int k = 0;k < urlArr.length; k++) {
				URL url = new URL(urlArr[k]);
				HttpURLConnection uc = (java.net.HttpURLConnection) url.openConnection();
				uc.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "MS950"));
				String line = null;
				int count = 0;
				while ((line = in.readLine()) != null) {
					if (count > 0) {
						String[] stockData = line.substring(1, line.length() - 1).split("\",\"");
						StockVO vo = new StockVO();
						String banNo = stockData[5];
						if (stockList.stream().noneMatch(a -> StringUtils.equals(a.getBanNo(), banNo))) {
							vo.setUpdateDate(DateUtil.twslash2UtilDate(stockData[0]));
							vo.setStockId(stockData[1]);
							vo.setCmpyName(stockData[2]);
							vo.setIndustry(stockData[3]);
							vo.setAddr(stockData[4]);
							vo.setBanNo(stockData[5]);
							vo.setChairman(stockData[6]);
							vo.setGeneralManager(stockData[7]);
							vo.setSpokesman(stockData[8]);
							vo.setSpokesmanPosition(stockData[9]);
							vo.setProxySpokesman(stockData[10]);
							vo.setSwitchboard(stockData[11]);
							vo.setEstablishmentDate(stockData[12]);
							vo.setListingDate(stockData[13]);
							vo.setStockPrice(stockData[14]);
							vo.setCapital(stockData[15]);
							vo.setPrivateShare(stockData[16]);
							vo.setSpecialShare(stockData[17]);
							vo.setFinancialReport(stockData[18]);
							vo.setStockTransferPlace(stockData[19]);
							vo.setTransferTel(stockData[20]);
							vo.setTransferAddr(stockData[21]);
							vo.setEnRefer(stockData[25]);
							vo.setEnAddr(stockData[26]);
							vo.setTaxTel(stockData[27]);
							vo.setEmail(stockData[28]);
							vo.setWebsite(stockData[29]);
							
							vo.setDataSource(dataSource);
							vo.setDataSourceUrl(dataSourceUrlArr[k]);
							vo.setStockType(stockTypeArr[k]);
							vo.setStockName(stockNameMap.get(vo.getStockId()));
							stockList.add(vo);
						}
					}
					count++;
				}
				
				
				in.close();
				uc.disconnect();
				Thread.sleep(5000);
			}
			ftsDao.insertToStock(stockList);
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
	}
	
	private Map<String,String> getStockNameList() throws Exception {
		Map<String,String> stockNameMap = new HashMap<>();
		String[] urlArr = {"http://isin.twse.com.tw/isin/C_public.jsp?strMode=2","http://isin.twse.com.tw/isin/C_public.jsp?strMode=4",
						   "http://isin.twse.com.tw/isin/C_public.jsp?strMode=5","http://isin.twse.com.tw/isin/C_public.jsp?strMode=1"};
		try {
			for (String Url : urlArr) {
				StringBuffer data = new StringBuffer();
				URL url = new URL(Url);
				HttpURLConnection uc = (java.net.HttpURLConnection) url.openConnection();
				uc.connect();
				BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "MS950"));
				String line = null;
				while ((line = in.readLine()) != null) {
					data.append(line);
				}
				for (int i = 1; i < data.toString().split("<tr><td bgcolor=#FAFAD2>").length; i++) {
					String row = data.toString().split("<tr><td bgcolor=#FAFAD2>")[i];
					String column = row.split("</td>")[0].replaceAll("　", "");
					if (!column.substring(0, 5).matches("[a-zA-Z0-9|\\.]*") || column.indexOf("KY") > -1 || column.indexOf("M31") > -1) {
						stockNameMap.put(column.substring(0, 4), column.substring(4));
					}
				}
				uc.disconnect();
				Thread.sleep(5000);
			}
			stockNameMap.put("8466", "美吉吉-KY");
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		return stockNameMap;
	}
	
	public String updateTradeDetail() throws Exception {
		List<TradeVO> tradeList = new ArrayList<>();
		String urlPath = "https://quality.data.gov.tw/dq_download_csv.php?nid=79641&md5_url=c336681e3285d53129bf04c77940dca9";
		String dataSource = "經濟部國際貿易局";
		String dataSourceUrl = "https://data.gov.tw/dataset/79641";
		File tempFile = new File("D:/temp.csv");
		try {
			SslUtil.ignoreSsl();
			URL url = new URL(urlPath);
			HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
		    conn.setDoInput(true);
		    conn.connect();
		    
		    FileUtils.copyInputStreamToFile(conn.getInputStream(), tempFile);
		    conn.disconnect();
		    Thread.sleep(10000);
		    
		    CsvReader csvReader = new CsvReader();
		    CsvContainer csv = csvReader.read(tempFile, StandardCharsets.UTF_8);
		    int count = 0;
		    for (CsvRow row : csv.getRows()) {
		    	if (count > 0) {
		        	TradeVO tradeVO = new TradeVO();
		        	tradeVO.setBanNo(row.getField(0));
		        	tradeVO.setRegDate(DateUtil.tw2Timestamp(row.getField(1)));
		        	tradeVO.setPermitDate(DateUtil.tw2Timestamp(row.getField(2)));
		        	tradeVO.setCmpyName(row.getField(3));
		        	tradeVO.setCmpyNameEn(row.getField(4));
		        	tradeVO.setAddress(row.getField(5));
		        	tradeVO.setAddressEn(row.getField(6));
		        	tradeVO.setRepresentative(StringUtils.isEmpty(row.getField(7)) ? null : row.getField(7));
		        	tradeVO.setTel(StringUtils.isEmpty(row.getField(8)) ? null : row.getField(8));
		        	tradeVO.setTaxTel(StringUtils.isEmpty(row.getField(9)) ? null : row.getField(9));
		        	tradeVO.setImportAuth("有".equals(row.getField(10)) ? "Y" : "N");
		        	tradeVO.setExportAuth("有".equals(row.getField(11)) ? "Y" : "N");
		        	tradeVO.setDataSource(dataSource);
		        	tradeVO.setDataSourceUrl(dataSourceUrl);
		        	tradeVO.setUpdateDate(DateUtil.tw2Timestamp(DateUtil.getTwToday(false)));
		        	tradeList.add(tradeVO);
		        }
		        count++;
		    }
		    
		    ftsDao.insertToTrade(tradeList);
		    
		    if (!tempFile.delete()) {
		    	return "remove file occur error , path in " + tempFile.getAbsolutePath();
		    }
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw e;
		}
		
		return "success";
	}
}
