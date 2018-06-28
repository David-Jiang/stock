package com.stock.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stock.vo.DisbandVO;
import com.stock.vo.GuildVO;

@Repository
@Transactional(value = "tm_crmsmoea",rollbackFor = Exception.class)
public class CrmsmoeaDao {
	@Autowired
	@Qualifier("jdbc_crmsmoea")
	private JdbcTemplate jdbcTemplate;

	private final Logger logger = LoggerFactory.getLogger("logs");

	public List<GuildVO> confirmGuildCmpyByCmpyName(List<GuildVO> gulidList) {
		String confirmsql = " select ban_no from cmainc_cmpy_info where cmpy_name like '%%' and ban_no is not null";
		try {
			for (GuildVO guildVO : gulidList) {
				if (StringUtils.isEmpty(guildVO.getBanNo()) && guildVO.getCmpyName().contains("公司")) {
					List<String> banNoList = jdbcTemplate.queryForList(
							confirmsql.replace("%%", guildVO.getCmpyName() + "%"), String.class, new Object[] {});
					if (CollectionUtils.isNotEmpty(banNoList)) {
						guildVO.setBanNo(banNoList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error("confirmGuildCmpyByCmpyName error");
		}
		return gulidList;
	}

	public void doDisband() throws Exception {
		String startNo = "'1073204204'";
		String endNo = "'1073204265'";
		
		String basesql = " SELECT ID_NO,MOEA_SEND_NO ";
		basesql += " FROM CDSC_DISBAND ";
		basesql += " WHERE REG_UNIT_CODE = '03' AND BATCH_NO = '107AL' AND MOEA_SEND_NO BETWEEN " + startNo + " AND " + endNo;
		List<DisbandVO> baseList = new ArrayList<>();
		
		String sourcesql = " SELECT A.ID_NO,B.RECV_SEQ,B.RCV_ORGN,B.RCV_ORGN_ADDR,IS_SEND_ARRIVE FROM CDSC_DISBAND_PROC A , CDOCC_DOC_RCVER B ";
		sourcesql += " WHERE A.REG_UNIT_CODE = '03' AND A.EXE_STATUS = 'BA' AND A.SENDING_NO = B.RCV_NO AND A.REG_UNIT_CODE = B.REG_UNIT_CODE AND B.RCV_TYPE = '1' ";
		sourcesql += " AND A.ID_NO IN ( ";
		sourcesql += "  SELECT ID_NO FROM CDSC_DISBAND WHERE REG_UNIT_CODE = '03' AND BATCH_NO = '107AL' AND MOEA_SEND_NO BETWEEN " + startNo + " AND " + endNo;
		sourcesql += " ) ";
		List<DisbandVO> sourceList = new ArrayList<>();
		try {
			List<Map<String, Object>> list1 = jdbcTemplate.queryForList(basesql);
			for (Map<String, Object> map : list1) {
				DisbandVO disbandVO = new DisbandVO();
				disbandVO.setIdNo((String) map.get("ID_NO"));
				disbandVO.setRcvNo((String) map.get("MOEA_SEND_NO"));
				baseList.add(disbandVO);
			}
			
			List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sourcesql);
			for (Map<String, Object> map : list2) {
				DisbandVO disbandVO = new DisbandVO();
				disbandVO.setIdNo((String) map.get("ID_NO"));
				disbandVO.setRecvSeq(BigDecimal.valueOf(Long.parseLong(map.get("RECV_SEQ").toString())));
				disbandVO.setRcvOrgn((String) map.get("RCV_ORGN"));
				disbandVO.setRcvOrgnAddr((String) map.get("RCV_ORGN_ADDR"));
				disbandVO.setIsSendArrive((String) map.get("IS_SEND_ARRIVE"));
				sourceList.add(disbandVO);
			}
			jdbcTemplate.update("DELETE FROM CDOCC_DOC_RCVER WHERE REG_UNIT_CODE = '03' AND RCV_TYPE = '1' AND RCV_NO BETWEEN " + startNo + " AND " + endNo);
			
			for (DisbandVO baseVO : baseList) {
				for (DisbandVO sourceVO : sourceList) {
					if (StringUtils.equals(baseVO.getIdNo(), sourceVO.getIdNo())) {
						jdbcTemplate.update(
								" insert into CDOCC_DOC_RCVER (REG_UNIT_CODE,RCV_NO,DFT_SEQ_NO,RCV_TYPE,RECV_SEQ,RCV_ORGN,RCV_ORGN_ADDR,IS_SEND_ARRIVE,ISSUE_TYPE)"
							  + " values ('03',?,'0','1',?,?,?,?,'0')",
								new Object[] { baseVO.getRcvNo(),
										sourceVO.getRecvSeq(), sourceVO.getRcvOrgn(), sourceVO.getRcvOrgnAddr(), sourceVO.getIsSendArrive()});
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}
