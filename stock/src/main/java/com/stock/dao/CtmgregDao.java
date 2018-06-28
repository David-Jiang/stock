package com.stock.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stock.vo.CtmgregVO;


@Repository
@Transactional(rollbackFor = Exception.class)
public class CtmgregDao {
	@Autowired
	@Qualifier("jdbc_ctmgreg")
	private JdbcTemplate jdbcTemplate;
	
	public List<CtmgregVO> queryAllcaseTimes(String westYearMonth) throws Exception {
		String startTime = westYearMonth + "01";
		String endTime = String.valueOf(Integer.valueOf(westYearMonth) + 1) + "01";
		StringBuffer sql = new StringBuffer();
		sql.append(" select A.reg_unit_code, IFNULL(B.cnt,0) as case_count ");
		sql.append(" from (select reg_unit_code from psrd_regunit) A ");
		sql.append(" left join (select reg_unit_code, count(*) as cnt from psrm_case_rcv ");
		sql.append(" where rcv_time >= STR_TO_DATE(?,'%Y%m%d') and rcv_time < STR_TO_DATE(?,'%Y%m%d') group by reg_unit_code) B ");
		sql.append(" on A.reg_unit_code = B.reg_unit_code order by A.reg_unit_code ");
		
		List<CtmgregVO> list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<CtmgregVO>(CtmgregVO.class), new Object[]{startTime, endTime});
		
		return list;
	}
	
	public List<CtmgregVO> queryAllcaseCount(String westYearMonth) throws Exception {
		String startTime = westYearMonth + "01";
		String endTime = String.valueOf(Integer.valueOf(westYearMonth) + 1) + "01";
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.vouch_kind, (select c1.code_name from psrd_codemapping c1 where c1.reg_unit_code = a.reg_unit_code and c1.kind='VOUCH' and c1.code = a.vouch_kind and c1.enable='Y') as vouch_kind_name, ");
		sql.append(" a.case_kind,  (select c21.code_name from psrd_codemapping c21 where c21.reg_unit_code = a.reg_unit_code and c21.kind='CASE' and c21.code = a.case_kind and c21.enable='Y') as case_kind_name, ");
		sql.append(" count(a.docu_key) as count from ( ");
		sql.append(" select reg_unit_code, vouch_kind, case_kind, Docu_Key from psrm_case_rcv where rcv_time >= ? and rcv_time < ? ) a ");
		sql.append(" group by  vouch_kind, vouch_kind_name , case_kind, case_kind_name order by  vouch_kind, vouch_kind_name , case_kind, case_kind_name ");
		
		List<CtmgregVO> list = jdbcTemplate.query(sql.toString(), new BeanPropertyRowMapper<CtmgregVO>(CtmgregVO.class), new Object[]{startTime, endTime});
		
		return list;
	}
}
