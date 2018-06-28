package com.stock.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.stock.dao.CtmgDao;
import com.stock.dao.CtmgoaDao;
import com.stock.dao.CtmgregDao;
import com.stock.dao.PubftsDao;
import com.stock.util.DateUtil;
import com.stock.util.PrintUtil;
import com.stock.vo.CtmgVO;
import com.stock.vo.CtmgoaVO;
import com.stock.vo.CtmgregVO;
import com.stock.vo.PubftsVO;

import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;

@Component
public class Report {
	@Autowired
    private PubftsDao pubftsDao;
	
	@Autowired
    private CtmgDao ctmgDao;
	
	@Autowired
    private CtmgregDao ctmgregDao;
	
	@Autowired
	private CtmgoaDao ctmgoaDao;
	
	private final Logger logger = LoggerFactory.getLogger("logs");
	
	public void fts(final HttpServletResponse response) throws Exception {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		int nowYear = Integer.valueOf(DateUtil.getTwToday(false).substring(0, 3));
		List<PubftsVO> list = null;
		try {
			list = pubftsDao.getQueryTimes(nowYear);
			list.removeIf(vo -> {
			      return StringUtils.equals(vo.getStatistcDate(), DateUtil.getTwToday(false).substring(0, 5));
			});
			parameterMap.put("PRINT_DATE", DateUtil.getTwToday(true).substring(0, 13));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		JRXlsExporter exporter = PrintUtil.exportExcel(response, "fts/fts_queryTimesRpt", "FTS查詢次數", list, parameterMap);
		exporter.exportReport();
	}
	
	public void ctmg(final HttpServletResponse response) throws Exception {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		int nowYear = Integer.valueOf(DateUtil.getTwToday(false).substring(0, 3));
		List<CtmgVO> list = null;
		try {
			list = ctmgDao.getQueryTimes();
			parameterMap.put("REMARK", "註:截至"+DateUtil.twDateWithDescription(String.valueOf(nowYear - 1) + "1231", false)+"止，全國動產擔保交易公示查詢網站查詢次數累計為" + NumberFormat.getInstance().format(ctmgDao.queryLastYearQueryTimes()) + "。");
			parameterMap.put("QUERY_YEAR", nowYear + "年");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		JRXlsExporter exporter = PrintUtil.exportExcel(response, "ctmg/ctmg_queryTimesRpt", "公示查詢累計次數", list, parameterMap);
		exporter.exportReport();
	}
	
	public void ctmgrpt(final HttpServletResponse response) throws Exception {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		Map<String, String> jasperNameMap = new HashMap<String, String>();
		Map<String, List<?>> dataListMap = new HashMap<String, List<?>>();
		List<CtmgVO> sheet1list = null;
		List<CtmgregVO> sheet2list = new ArrayList<CtmgregVO>();
		List<CtmgVO> sheet3list = null;
		List<CtmgregVO> sheet4list = null;
		String nowYearMonth = DateUtil.getTwToday(false).substring(0, 5);
		String queryMonth = String.valueOf(Integer.valueOf(nowYearMonth.substring(3, 5)) - 1);
		try {
			sheet1list = ctmgDao.queryAllmonthTimes(String.valueOf(Integer.valueOf(nowYearMonth) - 1));
			
			sheet2list.add(new CtmgregVO());
			sheet2list.addAll(ctmgregDao.queryAllcaseTimes(String.valueOf(Integer.valueOf(DateUtil.getToday(false).substring(0, 6)) - 1)));
			int caseCount = 0;
			int onlineCount = 0;
			for (CtmgregVO ctmgoaVO : sheet2list) {
				if (StringUtils.isEmpty(ctmgoaVO.getRegUnitCode())) {
					continue;
				}
				caseCount += Integer.valueOf(ctmgoaVO.getCaseCount());
				ctmgoaVO.setRegUnitName(this.getRegUnitName(ctmgoaVO.getRegUnitCode()));
				for (CtmgoaVO oaVO : ctmgoaDao.queryAllcaseTimes(String.valueOf(Integer.valueOf(DateUtil.getToday(false).substring(0, 6)) - 1))) {
					if (StringUtils.equals(oaVO.getRegUnitCode(), ctmgoaVO.getRegUnitCode())) {
						ctmgoaVO.setOnlineCount(String.valueOf(oaVO.getCaseCount()));
						onlineCount += Integer.valueOf(oaVO.getCaseCount());
						break;
					}
				}
			}
			CtmgregVO regVO = new CtmgregVO();
			regVO.setRegUnitCode("總計");
			regVO.setRegUnitName("");
			regVO.setCaseCount(String.valueOf(caseCount));
			regVO.setOnlineCount(String.valueOf(onlineCount));
			sheet2list.add(regVO);
			
			sheet3list = ctmgDao.queryMonthDebt(DateUtil.getToday(false).substring(0, 6));
			
			sheet4list = ctmgregDao.queryAllcaseCount(String.valueOf(Integer.valueOf(DateUtil.getToday(false).substring(0, 6)) - 1));
			
			jasperNameMap.put("ctmgrpt/ctmgrpt_sheet1", "公示查詢次數");
			jasperNameMap.put("ctmgrpt/ctmgrpt_sheet2", "各機關案件");
			jasperNameMap.put("ctmgrpt/ctmgrpt_sheet3", "案件數及總擔保債權額");
			jasperNameMap.put("ctmgrpt/ctmgrpt_sheet4", "深入分析");
			dataListMap.put("ctmgrpt/ctmgrpt_sheet1", sheet1list);
			dataListMap.put("ctmgrpt/ctmgrpt_sheet2", sheet2list);
			dataListMap.put("ctmgrpt/ctmgrpt_sheet3", sheet3list);
			dataListMap.put("ctmgrpt/ctmgrpt_sheet4", sheet4list);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		
		if (queryMonth.length() == 1) {
			queryMonth = "0" + queryMonth;
		}
		parameterMap.put("SHEET1_TITLE", nowYearMonth.substring(0, 3) + "年月份");
		parameterMap.put("SHEET2_TITLE", "各機關" + queryMonth + "月份案件("+queryMonth+"/01~"+queryMonth+"/"+DateUtil.calEndDayOfMonth(queryMonth)+")");
		parameterMap.put("SHEET3_TITLE", Integer.valueOf(queryMonth) + "月份動產擔保登記案件數及總擔保債權額(依案件類別)");
		parameterMap.put("SHEET4_TITLE", DateUtil.twDateWithDescription(String.valueOf(Integer.valueOf(nowYearMonth) - 1) + "01", false).substring(0, 7) + "深入分析psrm_case_rcv 收文於(案件數及總擔保債權額)的關係");
		JRXlsExporter exporter = PrintUtil.exportSheetExcel(response, jasperNameMap, "動擔月報表", dataListMap, parameterMap);
		exporter.exportReport();
	}
	
	public void ctmgoa(final HttpServletResponse response) throws Exception {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		Map<String, String> jasperNameMap = new HashMap<String, String>();
		Map<String, List<?>> dataListMap = new HashMap<String, List<?>>();
		List<CtmgoaVO> sheet1list = null;
		List<CtmgoaVO> sheet2list = null;
		List<CtmgoaVO> sheet3list = null;
		List<CtmgoaVO> sheet4list = null;
		List<CtmgoaVO> sheet5list = null;
		try {
			sheet1list = ctmgoaDao.queryRegisterMember();
			sheet2list = ctmgoaDao.queryCaseStatus();
			sheet3list = ctmgoaDao.queryCaseType(parameterMap);
			sheet4list = ctmgoaDao.queryCasePaid();
			sheet5list = ctmgoaDao.queryOnlineSetCase(parameterMap);
			
			jasperNameMap.put("ctmgoa/ctmgoa_sheet1", "會員及授權");
			jasperNameMap.put("ctmgoa/ctmgoa_sheet2", "案件狀態");
			jasperNameMap.put("ctmgoa/ctmgoa_sheet3", "案件狀態(含未結案)");
			jasperNameMap.put("ctmgoa/ctmgoa_sheet4", "已繳費並已結案");
			jasperNameMap.put("ctmgoa/ctmgoa_sheet5", "線上設立結案案件");
			dataListMap.put("ctmgoa/ctmgoa_sheet1", sheet1list);
			dataListMap.put("ctmgoa/ctmgoa_sheet2", sheet2list);
			dataListMap.put("ctmgoa/ctmgoa_sheet3", sheet3list);
			dataListMap.put("ctmgoa/ctmgoa_sheet4", sheet4list);
			dataListMap.put("ctmgoa/ctmgoa_sheet5", sheet5list);
			parameterMap.put("SHEET1_TITLE", DateUtil.getTodayWithSlash());
			parameterMap.put("SHEET2_TITLE", DateUtil.getTodayWithSlash());
			parameterMap.put("SHEET3_TITLE", DateUtil.getTodayWithSlash());
			parameterMap.put("SHEET4_TITLE", DateUtil.getTodayWithSlash());
			parameterMap.put("SHEET5_TITLE", DateUtil.getTodayWithSlash());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		JRXlsExporter exporter = PrintUtil.exportSheetExcel(response, jasperNameMap, "動擔線上統計", dataListMap, parameterMap);
		exporter.exportReport();
	}
	
	public void say(final HttpServletResponse response, String startDate, String endDate) throws Exception {
		Map<String, Object> parameterMap = new HashMap<String, Object>();
		List<PubftsVO> dataList = null;
		try {
			dataList = pubftsDao.getIwannaSay(DateUtil.tw2WestDate(startDate),DateUtil.tw2WestDate(endDate));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw e;
		}
		JRXlsExporter exporter = PrintUtil.exportExcel(response, "say/say", endDate + "我有話要說", dataList, parameterMap);
		exporter.exportReport();
	}
	
	private String getRegUnitName (String regUnitCode) {
		String regUnitName = null;
		switch (regUnitCode) {
			case "0101":
				regUnitName = "臺北市";
				break;
			case "0202":
				regUnitName = "高雄市";
				break;
			case "0301":
				regUnitName = "中辦";
				break;
			case "0501":
				regUnitName = "新竹科園區";
				break;
			case "0601":
				regUnitName = "加工出口區";
				break;
			case "0602":
				regUnitName = "加工區台中";
				break;
			case "0603":
				regUnitName = "加工區高雄";
				break;
			case "0604":
				regUnitName = "加工區中港";
				break;
			case "0605":
				regUnitName = "加工區屏東";
				break;
			case "0801":
				regUnitName = "南部科園區";
				break;
			case "0901":
				regUnitName = "中部科園區";
				break;
			case "1701":
				regUnitName = "臺中市";
				break;
			case "2101":
				regUnitName = "臺南市";
				break;
			case "3101":
				regUnitName = "新北市";
				break;
			case "3201":
				regUnitName = "桃園市";
				break;
			case "5103":
				regUnitName = "屏東生技";
				break;
			default: 
				regUnitName = ""; 
		}
		return regUnitName;
	}
	
	private String getRegUnitNameCsm (String regUnitCode) {
		String regUnitName = null;
		switch (regUnitCode) {
			case "01":
				regUnitName = "商業司";
				break;
			case "02":
				regUnitName = "台北";
				break;
			case "03":
				regUnitName = "中辦";
				break;
			case "04":
				regUnitName = "高雄";
				break;
			case "05":
				regUnitName = "竹科";
				break;
			case "06":
				regUnitName = "加工";
				break;
			case "08":
				regUnitName = "南科";
				break;
			case "09":
				regUnitName = "中科";
				break;
			case "17":
				regUnitName = "台中";
				break;
			case "21":
				regUnitName = "台南";
				break;
			case "31":
				regUnitName = "新北";
				break;
			case "32":
				regUnitName = "桃園";
				break;
			default: 
				regUnitName = ""; 
		}
		return regUnitName;
	}
	
}
