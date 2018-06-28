package com.stock.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stock.util.DateUtil;
import com.stock.vo.CtmgVO;


@Repository
@Transactional(rollbackFor = Exception.class)
public class CtmgDao {
	@Autowired
	@Qualifier("jdbc_ctmg")
	private JdbcTemplate jdbcTemplate;

	public List<CtmgVO> getQueryTimes() throws Exception {
		String nowYearMonth = String.valueOf(Integer.valueOf(DateUtil.getTwToday(false).substring(0, 5)) - 1); 
		int startTime = 0;
		int lastTime = 0;
		if (StringUtils.equals(nowYearMonth.substring(3, 5), "00")) { 
			startTime = ((Integer.valueOf(nowYearMonth.substring(0, 3)) - 1) * 100) + 1; 
			lastTime = startTime + 11; 
		} else {
			startTime = Integer.valueOf(nowYearMonth.substring(0, 3) + "01");
			lastTime = Integer.valueOf(nowYearMonth);
		}
		
		
		String monthTimesSql = " SELECT SUM(COUNT_NUMBER) FROM CTML_COUNTER WHERE count_date like (?) ";
		String accmulateSql = " SELECT SUM(COUNT_NUMBER) FROM CTML_COUNTER WHERE count_date < ? ";
		List<CtmgVO> list = new ArrayList<CtmgVO>();
		for (int i = 0; i < (lastTime - startTime + 1); i++) {
			CtmgVO ctmgvo = new CtmgVO();
			ctmgvo.setMonthTimes(jdbcTemplate.queryForObject(monthTimesSql.toString(), Integer.class, new Object[]{(startTime + i) + "%"}));
			ctmgvo.setAllYearTimes(jdbcTemplate.queryForObject(accmulateSql.toString(), Integer.class, new Object[]{(startTime + i + 1) + "01"}));
			if (i == 0) {
				int lastMonthTimes = jdbcTemplate.queryForObject(monthTimesSql.toString(), Integer.class, new Object[]{(startTime - 89) + "%"});
				ctmgvo.setYearTimes(ctmgvo.getMonthTimes());
				ctmgvo.setGrowTimes(ctmgvo.getMonthTimes() - lastMonthTimes);
			} else {
				int monthTimes = ctmgvo.getMonthTimes();
				ctmgvo.setYearTimes(monthTimes + list.get(i-1).getYearTimes());
				ctmgvo.setGrowTimes(monthTimes - list.get(i-1).getMonthTimes());
			}
			list.add(ctmgvo);
		}
		return list;
	}
	
	public int queryLastYearQueryTimes() throws Exception {
		String accmulateSql = " SELECT SUM(COUNT_NUMBER) FROM CTML_COUNTER WHERE count_date < ? ";
		return jdbcTemplate.queryForObject(accmulateSql.toString(), Integer.class, new Object[]{DateUtil.getTwToday(false).substring(0, 3) + "0101"});
		
	}
	
	public List<CtmgVO> queryAllmonthTimes(String yearMonth) throws Exception {
		List<CtmgVO> list = new ArrayList<CtmgVO>();
		int year = Integer.valueOf(yearMonth.substring(0, 3)) * 100;
		String monthTimesSql = " SELECT SUM(COUNT_NUMBER) FROM CTML_COUNTER WHERE count_date like (?) ";
		for (int i = 1; i <= Integer.valueOf(yearMonth.substring(3, 5)); i++) {
			CtmgVO vo = new CtmgVO();
			vo.setMonthTimes(jdbcTemplate.queryForObject(monthTimesSql.toString(), Integer.class, new Object[]{(year + i) + "%"}));
			list.add(vo);
		}
		return list;
	}
	
	public List<CtmgVO> queryMonthDebt(String westYearMonth) throws Exception {
		String startTime = "";
		String endTime = "";
		if (Integer.parseInt(westYearMonth.substring(4, 6)) == 1) { 
			startTime = (Integer.valueOf(westYearMonth.substring(0, 4)) - 1) + "1201";
		} else {
			startTime = String.valueOf(Integer.valueOf(westYearMonth) - 1) + "01";
		}
		endTime = westYearMonth + "01";
		List<CtmgVO> list = new ArrayList<CtmgVO>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select trim(date_format(certificate_app_date,'%Y%m')-191100) as certAppYrMon, ");
		sql.append(" trim(date_format(CHANGE_APP_DATE,'%Y%m')-191100) as chngAppYrMon, case_type, ");
		sql.append(" (select code_name from ctmd_codemapping where code = case_type and kind = 'CASE') as case_name, count(*) AS COUNT, FORMAT(sum(CR_PRICE),0) AS AMT, CR_PRICE_CURR_CODE");
		sql.append(" from ctmm_case_main A, ctmm_case_target B ");
		sql.append(" where A.cancel_app_Date is null and B.target_type = '01' ");
		sql.append(" and A.certificate_App_No_Word = B.certificate_App_No_Word and A.reg_Unit_Code = B.reg_Unit_Code ");
		sql.append(" and ( ( certificate_app_date >= ? and certificate_app_date < ?) ");
		sql.append(" OR (CHANGE_APP_DATE >= ? and CHANGE_APP_DATE < ? )) and CR_PRICE_CURR_CODE <> 'USD' ");
		sql.append(" group by certAppYrMon, chngAppYrMon, case_type, CR_PRICE_CURR_CODE order by case_type, certAppYrMon, chngAppYrMon, CR_PRICE_CURR_CODE ");
		
		CtmgVO ctmgVO = new CtmgVO();
		long case1Count = 0;
		long case1Amt = 0;
		long case2Count = 0;
		long case2Amt = 0;
		long case3Count = 0;
		long case3Amt = 0;
		for (CtmgVO vo : jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<CtmgVO>(CtmgVO.class), new Object[]{startTime, endTime, startTime, endTime})) {
			if (StringUtils.equals(vo.getCaseType(), "01")) {
				case1Count += Long.valueOf(vo.getCount());
				case1Amt += Long.valueOf(StringUtils.defaultIfEmpty(vo.getAmt(), "0").replaceAll(",", ""));
			} else if (StringUtils.equals(vo.getCaseType(), "02")) {
				case2Count += Long.valueOf(vo.getCount());
				case2Amt += Long.valueOf(StringUtils.defaultIfEmpty(vo.getAmt(), "0").replaceAll(",", ""));
			} else if (StringUtils.equals(vo.getCaseType(), "03")) {
				case3Count += Long.valueOf(vo.getCount());
				case3Amt += Long.valueOf(StringUtils.defaultIfEmpty(vo.getAmt(), "0").replaceAll(",", ""));
			}
		}
		ctmgVO.setCase1Count(case1Count);
		ctmgVO.setCase1Amt(case1Amt);
		ctmgVO.setCase2Count(case2Count);
		ctmgVO.setCase2Amt(case2Amt);
		ctmgVO.setCase3Count(case3Count);
		ctmgVO.setCase3Amt(case3Amt);
		ctmgVO.setQueryTime(DateUtil.twDateWithDescription(DateUtil.west2TwDate(startTime), false).substring(0, 7));
		list.add(ctmgVO);
		
		return list;
	}
}
