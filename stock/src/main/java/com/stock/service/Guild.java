package com.stock.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.stock.dao.PubftsDao;
import com.stock.dao.IcbsDao;
import com.stock.dao.CrmsmoeaDao;
import com.stock.util.DateUtil;
import com.stock.vo.GuildVO;

@Component
public class Guild {
	
	@Autowired
    private PubftsDao pubftsDao;
	
	@Autowired
    private IcbsDao icbsDao;
	
	@Autowired
    private CrmsmoeaDao crmsmoeaDao;
	
	@SuppressWarnings("resource")
	public void importExcel() {
		String[] filepathArr = {"D:/guild/台灣醫療暨生技器材工業同業公會.xlsx","D:/guild/中華民國太陽熱能商業同業公會.xlsx","D:/guild/台灣區工具機暨零組件工業同業公會.xlsx","D:/guild/台灣區造船工業同業公會.xlsx",
				"D:/guild/台灣資源再生工業同業公會.xlsx","D:/guild/臺灣環保暨資源再生設備工業同業公會.xlsx","D:/guild/台灣區航太工業同業公會.xlsx"};
		String[] dataSourceUrlArr = {"http://www.tmbia.org.tw/","http://www.taiwansolar.org.tw/","http://www.tmba.org.tw/","http://www.tsba.org.tw/",
				"http://www.trria.org.tw/","http://tema.org.tw/","http://www.taia.org.tw/index.asp"};
		List<GuildVO> gulidList = new ArrayList<>();
		Date updateDate = DateUtil.tw2UtilDate("1060905");
		try {
			for (int k = 0; k < filepathArr.length; k++) {
				Workbook wb = null;
				String filepath = filepathArr[k];
				String ext = filepath.substring(filepath.lastIndexOf("."));
				InputStream is = new FileInputStream(new File(filepath));
				if (".xls".equals(ext)) {
	                wb = new HSSFWorkbook(is);
	            } else {
	                wb = new XSSFWorkbook(is);
	            }
				Sheet sheet = wb.getSheetAt(0);
				for (int i = 1; i <= sheet.getLastRowNum(); i++) {
					GuildVO vo = new GuildVO();
					String cmpyName = sheet.getRow(i).getCell(1).toString().trim();
					String product = sheet.getRow(i).getCell(2).toString().trim();
					String tel = sheet.getRow(i).getCell(3).toString().trim();
					String banNo = sheet.getRow(i).getCell(5).toString().trim();
					if (StringUtils.isEmpty(cmpyName)) {
						continue;
					} else {
						vo.setCmpyName(cmpyName.trim());
					}
					if (StringUtils.isNotBlank(product)) {
						vo.setProduct(product.replaceAll("．", "、").replaceAll("‧", "、"));
					}
					if (StringUtils.isNotBlank(tel)) {
						vo.setTel(tel);
					}
					if (banNo.length() == 8) {
						vo.setBanNo(banNo);
					}
					vo.setUpdateDate(updateDate);
					vo.setDataSource(filepath.split("/")[2].substring(0, filepath.split("/")[2].lastIndexOf(".")));
					vo.setDataSourceUrl(dataSourceUrlArr[k]);
					gulidList.add(vo);
				}
				is.close();
			}
			icbsDao.confirmGuildBusmByCmpyName(gulidList);
			crmsmoeaDao.confirmGuildCmpyByCmpyName(gulidList);
			gulidList.removeIf(vo -> StringUtils.isEmpty(vo.getBanNo()));
			pubftsDao.insertToGuild(gulidList, false);
			pubftsDao.removeDistinctGuild();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
	}
	
	public void parseXML() {
		try {
			List<String> cmpyNoList = new ArrayList<>();
			List<GuildVO> gulidList = new ArrayList<>();
			File f = new File("D:/getinfo.xml"); 
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance(); 
			DocumentBuilder builder = factory.newDocumentBuilder(); 
			Document doc = builder.parse(f); 
			NodeList node = doc.getElementsByTagName("ir");
			for (int i = 0;i < node.getLength(); i++){
				String cmpyNo = node.item(i).getAttributes().getNamedItem("m").getNodeValue();
				String cmpyName = node.item(i).getAttributes().getNamedItem("c").getNodeValue();
				cmpyNoList.add(cmpyNo + "-" + cmpyName);
			}
			
			cmpyNoList.add("55340-正河源機械配件有限公司");
			cmpyNoList.add("30996-企宏電工系統化股份有限公司");
			cmpyNoList.add("40651-兆奕精密機械有限公司");
			cmpyNoList.add("40647-宇厚企業有限公司");
			cmpyNoList.add("30977-沛霖科技有限公司");
			cmpyNoList.add("40645-松勤股份有限公司");
			cmpyNoList.add("40650-恆輪實業有限公司");
			cmpyNoList.add("40649-洋迅科技有限公司");
			cmpyNoList.add("55342-科基企業有限公司");
			cmpyNoList.add("40648-國際直線科技股份有限公司");
			cmpyNoList.add("80375-嘉頡金屬股份有限公司");
			cmpyNoList.add("30969-嶸澤機械股份有限公司");
			
			for (String cmpy : cmpyNoList) {
				HttpURLConnection uc = null;
				BufferedReader in = null;
				StringBuffer guildData = new StringBuffer();
				String cmpyName;
				String tel;
				String product;
				String website;
				String email;
				String staffCount;
				try {
					URL url = new URL("http://www.tami.org.tw/category/contact_2.php?on=1&ms=" + cmpy.split("-")[0]);
					uc = (java.net.HttpURLConnection) url.openConnection();
					uc.connect();
					in = new BufferedReader(new InputStreamReader(uc.getInputStream(),"utf-8"));
					String line = null;
					while ((line = in.readLine()) != null) {
						guildData.append(line);
					}
					cmpyName = guildData.toString().split("class=company-top>")[1].split("</SPAN>")[0].trim();
					tel = guildData.toString().split("<TD class=list_td width=213>")[1].split("</TD>")[0].trim();
					product = guildData.toString().split("<TD class=list_td colSpan=3>")[3].split("</TD>")[0].trim();
					product = product.replaceAll(",", "、");
					website = guildData.toString().split("class='tongue'>")[1].split("</A>")[0].trim();
					email = guildData.toString().split("class='tongue'>")[2].split("</A>")[0].trim();
					staffCount = guildData.toString().split("<TD class=list_td>")[6].split("</TD>")[0].trim();
				} catch (Exception e) {
					in.close();
					uc.disconnect();
					Thread.sleep(5000);
					continue;
				}
				GuildVO vo = new GuildVO();
				vo.setCmpyName(cmpyName);
				vo.setTel(tel);
				if (product.length() >= 1000) {
					vo.setProduct(product.substring(0, 1000));
				} else {
					vo.setProduct(product);
				}
				vo.setUpdateDate(DateUtil.tw2UtilDate("1061212"));
				vo.setDataSource("臺灣機械工業同業公會");
				vo.setDataSourceUrl("http://www.tami.org.tw");
				vo.setWebsite(website);
				vo.setEmail(email);
				vo.setStaffCount(staffCount);
				gulidList.add(vo);
				in.close();
				uc.disconnect();
				Thread.sleep(500);
			}
			icbsDao.confirmGuildBusmByCmpyName(gulidList);
			crmsmoeaDao.confirmGuildCmpyByCmpyName(gulidList);
			gulidList.removeIf(vo -> StringUtils.isEmpty(vo.getBanNo()));
			pubftsDao.insertToGuild(gulidList, true);
			pubftsDao.removeDistinctGuild();
		} catch (Exception e) {
			System.out.print(e.getMessage());
		}
	}
}
