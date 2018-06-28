package com.stock.dao;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stock.vo.GuildVO;


@Repository
@Transactional(rollbackFor = Exception.class)
public class IcbsDao {
	
	@Autowired
	@Qualifier("jdbc_icbs")
	private JdbcTemplate jdbcTemplate;
	
	private final Logger logger = LoggerFactory.getLogger("logs");
	
	public List<GuildVO> confirmGuildBusmByCmpyName(List<GuildVO> gulidList) throws Exception {
		String confirmsql = " select ban_no from busm_buss_main where buss_name like '%%' and ban_no is not null and IS_NEWEST = 'Y'";
		try {
			for (GuildVO guildVO : gulidList) {
				if (StringUtils.isEmpty(guildVO.getBanNo()) && !guildVO.getCmpyName().contains("公司")) {
					List<String> banNoList = jdbcTemplate.queryForList(confirmsql.replace("%%", guildVO.getCmpyName() + "%"), String.class, new Object[]{});
					if (CollectionUtils.isNotEmpty(banNoList)) {
						guildVO.setBanNo(banNoList.get(0));
					}
				}
			}
		} catch (Exception e) {
			logger.error("confirmGuildBusmByCmpyName error");
			throw e;
		}
		return gulidList;
	}
}
