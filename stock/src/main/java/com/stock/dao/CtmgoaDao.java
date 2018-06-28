package com.stock.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stock.util.DateUtil;
import com.stock.vo.CtmgoaVO;


@Repository
@Transactional(rollbackFor = Exception.class)
public class CtmgoaDao {
	@Autowired
	@Qualifier("jdbc_ctmgoa")
	private JdbcTemplate jdbcTemplate;
	
	public List<CtmgoaVO> queryAllcaseTimes(String westYearMonth) throws Exception {
		String startTime = westYearMonth + "01";
		String endTime = String.valueOf(Integer.valueOf(westYearMonth) + 1) + "01";
		StringBuffer sql = new StringBuffer();
		sql.append(" select A.reg_unit_code, IFNULL(B.caseCnt, 0) as case_count ");
		sql.append(" from (select reg_unit_code from psad_unit) A ");
		sql.append(" left join (select reg_unit_code, count(*) as caseCnt from psam_appl_main ");
		sql.append(" where ((case_status ='103' and apply_type != '02' and telix_no in (select telix_no from psal_trans_log where result='0000')) ");
		sql.append(" or (case_status ='103' and apply_type = '02')) ");
		sql.append(" and apply_date >= STR_TO_DATE(?,'%Y%m%d') and apply_date < STR_TO_DATE(?,'%Y%m%d') ");
		sql.append(" group by reg_unit_code) B on A.reg_unit_code = B.reg_unit_code order by reg_unit_code ");
		
		List<CtmgoaVO> list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<CtmgoaVO>(CtmgoaVO.class), new Object[]{startTime, endTime});
		
		return list;
	}
	
	public List<CtmgoaVO> queryRegisterMember() throws Exception {
		String currentCmpySql = " select count(*) as cnt from psam_regi_member where ban_no !='20828393' and keyin_time < DATE_FORMAT(now(),'%Y-%m-01') ";
		String currentAuthSql = " select count(*) as cnt from psam_auth_user where ban_no !='20828393' and  ban_no != Cast( AES_DECRYPT(UNHEX(id_no),'psa') as char) and keyin_time < DATE_FORMAT(now(),'%Y-%m-01') ";
		String currentBankSql = " select  count(*) as cnt from psam_regi_member where cmpy_name like '%銀行%' and keyin_time < DATE_FORMAT(now(),'%Y-%m-01') ";
		String monthCmpySql = " select count(*) as cnt from psam_regi_member A where A.ban_no !='20828393' and A.keyin_time >= date_sub(date_sub(date_format(now(),'%y-%m-%d'),interval extract( day from now())-1 day),interval 1 month) and A.keyin_time < DATE_FORMAT(now(),'%Y-%m-01') and A.ban_no not in (select distinct Ban_no from psah_regi_member) ";
		String monthAuthSql = " select count(*) as cnt from psam_auth_user where ban_no !='20828393' and ban_no != Cast( AES_DECRYPT(UNHEX(id_no),'psa') as char) and keyin_time >= date_sub(date_sub(date_format(now(),'%y-%m-%d'),interval extract( day from now())-1 day),interval 1 month) and keyin_time < DATE_FORMAT(now(),'%Y-%m-01') and id_no not in (select distinct id_no from psah_auth_user) ";
		String monthBankSql = " select count(*) as cnt from psam_regi_member A where  cmpy_name like '%銀行%' and A.ban_no !='20828393' and A.keyin_time >= date_sub(date_sub(date_format(now(),'%y-%m-%d'),interval extract( day from now())-1 day),interval 1 month) and A.keyin_time < DATE_FORMAT(now(),'%Y-%m-01') ";
		
		CtmgoaVO ctmgoaVO = new CtmgoaVO();
		ctmgoaVO.setCurrentCmpyCount(jdbcTemplate.queryForObject(currentCmpySql, Integer.class));
		ctmgoaVO.setCurrentAuthCount(jdbcTemplate.queryForObject(currentAuthSql, Integer.class));
		ctmgoaVO.setCurrentBankCount(jdbcTemplate.queryForObject(currentBankSql, Integer.class));
		ctmgoaVO.setMonthCmpyCount(jdbcTemplate.queryForObject(monthCmpySql, Integer.class));
		ctmgoaVO.setMonthAuthCount(jdbcTemplate.queryForObject(monthAuthSql, Integer.class));
		ctmgoaVO.setMonthBankCount(jdbcTemplate.queryForObject(monthBankSql, Integer.class));
		ctmgoaVO.setQueryTime(DateUtil.twDateWithDescription(String.valueOf(Integer.valueOf(DateUtil.getTwToday(false).substring(0, 5)) - 1) + "01", false).substring(0, 7));
		List<CtmgoaVO> list = new ArrayList<CtmgoaVO>();
		list.add(ctmgoaVO);
		return list;
	}
	
	public List<CtmgoaVO> queryCaseStatus() throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select X.reg_unit_code, X.code as case_status , ifnull(Y.cnt,0) as case_count from ( ");
		sql.append(" select A.*, B.* from  (select reg_unit_code from psad_unit ) A JOIN (select code from psad_codemapping where kind='STATUS') B ");
		sql.append(" ) X ");
		sql.append(" LEFT OUTER JOIN ( ");
		sql.append(" select reg_unit_code, case_status, count(*) as cnt  from psam_appl_main  where create_time < DATE_FORMAT(now(),'%Y-%m-01') group by  case_status, reg_unit_code ");
		sql.append(" ) Y ");
		sql.append(" ON X.code = Y.case_status and X.reg_unit_code = Y.reg_unit_code order by x.code, X.reg_unit_code ");
		Map<String, CtmgoaVO> regUnitMap = new HashMap<String, CtmgoaVO>();
		for (CtmgoaVO oaVO : jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<CtmgoaVO>(CtmgoaVO.class))) {
			if (!regUnitMap.containsKey(oaVO.getRegUnitCode())) {
				CtmgoaVO vo = new CtmgoaVO();
				vo.setRegUnitCode(oaVO.getRegUnitCode());
				vo.setRegUnitName(this.getRegUnitName(oaVO.getRegUnitCode()));
				regUnitMap.put(oaVO.getRegUnitCode(), vo);
			}
			CtmgoaVO vo = regUnitMap.get(oaVO.getRegUnitCode());
			if (StringUtils.equals(oaVO.getCaseStatus(), "001")) {
				vo.setCase1(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "002")) {
				vo.setCase2(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "003")) {
				vo.setCase3(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "004")) {
				vo.setCase4(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "005")) {
				vo.setCase5(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "006")) {
				vo.setCase6(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "101")) {
				vo.setCase7(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "102")) {
				vo.setCase8(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "103")) {
				vo.setCase9(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "105")) {
				vo.setCase10(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "106")) {
				vo.setCase11(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "201")) {
				vo.setCase12(oaVO.getCaseCount());
			}
		}
		List<CtmgoaVO> list = new ArrayList<CtmgoaVO>(regUnitMap.values());
		Collections.sort(list, (p1, p2) -> Integer.valueOf(p1.getRegUnitCode()).compareTo(Integer.valueOf(p2.getRegUnitCode())));
		return list;
	}
	
	public List<CtmgoaVO> queryCaseType(Map<String, Object> parameterMap) throws Exception {
		StringBuffer countSql = new StringBuffer();
		countSql.append(" select A.reg_unit_code, A.code as case_status, IFNULL(B.caseCnt, 0) as case_count from (select reg_unit_code, code, 0 as caseCnt ");
		countSql.append(" from psad_unit, psad_codemapping where kind='APPLY') A left join (select reg_unit_code, apply_type, count(*) as caseCnt ");
		countSql.append(" from psam_appl_main where case_status !='006'  and create_time < DATE_FORMAT(now(),'%Y-%m-01') group by apply_type, reg_unit_code ");
		countSql.append(" order by apply_type, reg_unit_code) B on A.reg_unit_code = B.reg_unit_code and A.code = B.apply_type order by A.code, A.reg_unit_code ");
		
		StringBuffer amtSql = new StringBuffer();
		amtSql.append(" select reg_unit_code, apply_type as case_status,  cr_price_curr_code, sum(cr_price) as amt ");
		amtSql.append(" from psam_appl_main where case_status !='006'  and create_time < DATE_FORMAT(now(),'%Y-%m-01') ");
		amtSql.append(" group by apply_type, reg_unit_code, cr_price_curr_code order by apply_type, reg_unit_code, cr_price_curr_code");
		
		Map<String, CtmgoaVO> regUnitMap = new HashMap<String, CtmgoaVO>();
		for (CtmgoaVO oaVO : jdbcTemplate.query(countSql.toString(), new BeanPropertyRowMapper<CtmgoaVO>(CtmgoaVO.class))) {
			if (!regUnitMap.containsKey(oaVO.getRegUnitCode())) {
				CtmgoaVO vo = new CtmgoaVO();
				vo.setRegUnitCode(oaVO.getRegUnitCode());
				vo.setRegUnitName(this.getRegUnitName(oaVO.getRegUnitCode()));
				regUnitMap.put(oaVO.getRegUnitCode(), vo);
			}
			CtmgoaVO vo = regUnitMap.get(oaVO.getRegUnitCode());
			if (StringUtils.equals(oaVO.getCaseStatus(), "00")) {
				vo.setCase1(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "01")) {
				vo.setCase2(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "02")) {
				vo.setCase3(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "07")) {
				vo.setCase4(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "98")) {
				vo.setCase5(oaVO.getCaseCount());
			}
		}
		
		long CASE1_AMT_ALL = 0;
		long CASE1_AMT_TW = 0;
		long CASE2_AMT_ALL = 0;
		long CASE2_AMT_TW = 0;
		long CASE3_AMT_ALL = 0;
		long CASE3_AMT_TW = 0;
		for (CtmgoaVO oaVO : jdbcTemplate.query(amtSql.toString(), new BeanPropertyRowMapper<CtmgoaVO>(CtmgoaVO.class))) {
			long amt = 0;
			if (oaVO.getAmt() != null) {
				amt = oaVO.getAmt().longValue();
			}
			if (StringUtils.equals(oaVO.getCaseStatus(), "00")) {
				if (StringUtils.equals(oaVO.getCrPriceCurrCode(), "TWD")) {
					CASE1_AMT_TW += amt;
				}
				CASE1_AMT_ALL += amt;
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "01")) {
				if (StringUtils.equals(oaVO.getCrPriceCurrCode(), "TWD")) {
					CASE2_AMT_TW += amt;
				}
				CASE2_AMT_ALL += amt;
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "02")) {
				if (StringUtils.equals(oaVO.getCrPriceCurrCode(), "TWD")) {
					CASE3_AMT_TW += amt;
				}
				CASE3_AMT_ALL += amt;
			}
		}
		parameterMap.put("CASE1_AMT_ALL", CASE1_AMT_ALL);
		parameterMap.put("CASE1_AMT_TW", CASE1_AMT_TW);
		parameterMap.put("CASE2_AMT_ALL", CASE2_AMT_ALL);
		parameterMap.put("CASE2_AMT_TW", CASE2_AMT_TW);
		parameterMap.put("CASE3_AMT_ALL", CASE3_AMT_ALL);
		parameterMap.put("CASE3_AMT_TW", CASE3_AMT_TW);
		
		
		List<CtmgoaVO> list = new ArrayList<CtmgoaVO>(regUnitMap.values());
		Collections.sort(list, (p1, p2) -> Integer.valueOf(p1.getRegUnitCode()).compareTo(Integer.valueOf(p2.getRegUnitCode())));
		return list;
	}
	
	public List<CtmgoaVO> queryCasePaid() throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select A.reg_unit_code, A.code as case_status, IFNULL(B.caseCnt, 0) as case_count from (select reg_unit_code, code, 0 as caseCnt ");
		sql.append(" from psad_unit, psad_codemapping where kind='APPLY') A left join ( ");
		sql.append(" select reg_unit_code, apply_type, count(*) as caseCnt from psam_appl_main where create_time < DATE_FORMAT(now(),'%Y-%m-01') ");
		sql.append(" and ( (case_status ='103' and apply_type != '02' and telix_no in (select telix_no from psal_trans_log where result='0000') )");
		sql.append(" or (case_status ='103' and apply_type = '02') ) group by reg_unit_code, apply_type ) B ");
		sql.append(" on A.reg_unit_code = B.reg_unit_code and A.code = B.apply_type order by A.code, A.reg_unit_code ");
		
		Map<String, CtmgoaVO> regUnitMap = new HashMap<String, CtmgoaVO>();
		for (CtmgoaVO oaVO : jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<CtmgoaVO>(CtmgoaVO.class))) {
			if (!regUnitMap.containsKey(oaVO.getRegUnitCode())) {
				CtmgoaVO vo = new CtmgoaVO();
				vo.setRegUnitCode(oaVO.getRegUnitCode());
				vo.setRegUnitName(this.getRegUnitName(oaVO.getRegUnitCode()));
				regUnitMap.put(oaVO.getRegUnitCode(), vo);
			}
			CtmgoaVO vo = regUnitMap.get(oaVO.getRegUnitCode());
			if (StringUtils.equals(oaVO.getCaseStatus(), "00")) {
				vo.setCase1(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "01")) {
				vo.setCase2(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "02")) {
				vo.setCase3(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "07")) {
				vo.setCase4(oaVO.getCaseCount());
			} else if (StringUtils.equals(oaVO.getCaseStatus(), "98")) {
				vo.setCase5(oaVO.getCaseCount());
			}
		}
		List<CtmgoaVO> list = new ArrayList<CtmgoaVO>(regUnitMap.values());
		Collections.sort(list, (p1, p2) -> Integer.valueOf(p1.getRegUnitCode()).compareTo(Integer.valueOf(p2.getRegUnitCode())));
		return list;
	}
	
	public List<CtmgoaVO> queryOnlineSetCase(Map<String, Object> parameterMap) throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" select A.reg_unit_code, ifnull(B.cnt,0) as case_count, B.cr_price_curr_code, ifnull(B.sumprice,0) as amt from psad_unit A ");
		sql.append(" left outer join( select reg_unit_code, apply_type,  count(*) as cnt, cr_price_curr_code,  sum(cr_price) as sumprice ");
		sql.append(" from psam_appl_main where case_status ='103' and apply_type ='00' and  create_time < DATE_FORMAT(now(),'%Y-%m-01') ");
		sql.append(" and telix_no in (select telix_no from psal_trans_log where result='0000') ");
		sql.append(" group by apply_type, reg_unit_code, cr_price_curr_code ) B");
		sql.append(" on A.reg_unit_code = B.reg_unit_code order by  A.reg_unit_code, apply_type, cr_price_curr_code ");
		
		Map<String, CtmgoaVO> regUnitMap = new HashMap<String, CtmgoaVO>();
		for (CtmgoaVO oaVO : jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<CtmgoaVO>(CtmgoaVO.class))) {
			if (!regUnitMap.containsKey(oaVO.getRegUnitCode())) {
				CtmgoaVO vo = new CtmgoaVO();
				vo.setRegUnitCode(oaVO.getRegUnitCode());
				vo.setRegUnitName(this.getRegUnitName(oaVO.getRegUnitCode()));
				regUnitMap.put(oaVO.getRegUnitCode(), vo);
			}
			CtmgoaVO vo = regUnitMap.get(oaVO.getRegUnitCode());
			if (StringUtils.equals(oaVO.getCrPriceCurrCode(), "USD")) {
				vo.setAmtUSD(oaVO.getAmt());
			} else {
				vo.setAmt(oaVO.getAmt());
			}
			vo.setCaseCount(vo.getCaseCount() + oaVO.getCaseCount());
		}
		parameterMap.put("SHEET5_HEADER", "上線至" + (Integer.valueOf(DateUtil.getToday(false).substring(4, 6)) - 1) + "月底，線上設立結案的案件總計");
		
		List<CtmgoaVO> list = new ArrayList<CtmgoaVO>(regUnitMap.values());
		Collections.sort(list, (p1, p2) -> Integer.valueOf(p1.getRegUnitCode()).compareTo(Integer.valueOf(p2.getRegUnitCode())));
		return list;
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
}
