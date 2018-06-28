package com.stock.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.stock.vo.GuildVO;
import com.stock.vo.PubftsVO;
import com.stock.vo.StockVO;
import com.stock.vo.TradeVO;

@Repository
@Transactional(rollbackFor = Exception.class)
public class PubftsDao {

	@Autowired
	@Qualifier("jdbc_pubfts")
	private JdbcTemplate jdbcTemplate;

	private final Logger logger = LoggerFactory.getLogger("logs");

	public void insertToStock(List<StockVO> stockList) throws Exception {
		String sql = "INSERT INTO PSYSM_STOCK_DETAIL VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

		try {
			jdbcTemplate.update(" DELETE FROM PSYSM_STOCK_DETAIL ");
			for (StockVO stockVO : stockList) {
				Object[] obj = { stockVO.getBanNo(), stockVO.getStockId(), stockVO.getStockName(),
						stockVO.getUpdateDate(), stockVO.getIndustry(), stockVO.getAddr(), stockVO.getChairman(),
						stockVO.getGeneralManager(), stockVO.getSpokesman(), stockVO.getSpokesmanPosition(),
						stockVO.getProxySpokesman(), stockVO.getSwitchboard(), stockVO.getEstablishmentDate(),
						stockVO.getListingDate(), stockVO.getStockPrice(), stockVO.getCapital(),
						stockVO.getPrivateShare(), stockVO.getSpecialShare(), stockVO.getFinancialReport(),
						stockVO.getStockTransferPlace(), stockVO.getTransferTel(), stockVO.getTransferAddr(),
						stockVO.getEnRefer(), stockVO.getEnAddr(), stockVO.getTaxTel(), stockVO.getEmail(),
						stockVO.getWebsite(), stockVO.getDataSource(), stockVO.getDataSourceUrl(),
						stockVO.getStockType(), stockVO.getCmpyName() };
				jdbcTemplate.update(sql, obj);
			}
		} catch (Exception e) {
			logger.error("insertToStock error");
			throw e;
		}
	}

	public void insertToGuild(List<GuildVO> gulidList, boolean addtional) throws Exception {
		String sql = "INSERT INTO PSYSM_GUILD_DETAIL(BAN_NO,CMPY_NAME,PRODUCT,TEL,DATA_SOURCE,DATA_SOURCE_URL,UPDATE_DATE) VALUES(?,?,?,?,?,?,?) ";
		String addtionalsql = "INSERT INTO PSYSM_GUILD_DETAIL VALUES(?,?,?,?,?,?,?,?,?,?) ";

		try {
			for (GuildVO guildVO : gulidList) {
				if (addtional) {
					Object[] obj = { guildVO.getBanNo(), guildVO.getCmpyName(), guildVO.getProduct(), guildVO.getTel(),
							guildVO.getDataSource(), guildVO.getDataSourceUrl(), guildVO.getUpdateDate(),
							guildVO.getWebsite(), guildVO.getEmail(), guildVO.getStaffCount() };
					jdbcTemplate.update(addtionalsql, obj);
				} else {
					Object[] obj = { guildVO.getBanNo(), guildVO.getCmpyName(), guildVO.getProduct(), guildVO.getTel(),
							guildVO.getDataSource(), guildVO.getDataSourceUrl(), guildVO.getUpdateDate() };
					jdbcTemplate.update(sql, obj);
				}
			}
		} catch (Exception e) {
			logger.error("insertToGuild error");
			throw e;
		}
	}

	public void removeDistinctGuild() throws Exception {
		String sql = "SELECT BAN_NO FROM PSYSM_GUILD_DETAIL GROUP BY BAN_NO HAVING COUNT(BAN_NO) > 1 AND COUNT(DATA_SOURCE) = 1";
		String querySql = "SELECT * FROM PSYSM_GUILD_DETAIL WHERE BAN_NO = ? AND ROWNUM = 1";
		String deleteSql = "DELETE FROM PSYSM_GUILD_DETAIL WHERE BAN_NO = ? ";
		List<GuildVO> guildList = new ArrayList<>();
		try {
			List<String> distinctBanNoList = jdbcTemplate.queryForList(sql, String.class);
			distinctBanNoList.forEach(banNo -> {
				guildList.add(jdbcTemplate.queryForObject(querySql, new BeanPropertyRowMapper<GuildVO>(GuildVO.class),
						new Object[] { banNo }));

			});
			distinctBanNoList.forEach(banNo -> {
				jdbcTemplate.update(deleteSql, new Object[] { banNo });
			});
			this.insertToGuild(guildList, true);
		} catch (Exception e) {
			logger.error("selectDistinctGuild error");
			throw e;
		}
	}

	public void insertToTrade(List<TradeVO> tradeList) throws Exception {
		String insertSql = "INSERT INTO PSYSM_TRADE_DETAIL VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
		String deleteSql = " DELETE FROM PSYSM_TRADE_DETAIL ";
		DataSource ds = jdbcTemplate.getDataSource();
		Connection con = ds.getConnection();
		con.setAutoCommit(false);
		try {
			PreparedStatement deletePs = con.prepareStatement(deleteSql);
			deletePs.executeUpdate();
			con.commit();
			
			
			
			PreparedStatement ps = con.prepareStatement(insertSql);
			int count = 1;
			for (TradeVO tradeVO : tradeList) {
				ps.setString(1, tradeVO.getBanNo());
				ps.setTimestamp(2, tradeVO.getRegDate());
				ps.setTimestamp(3, tradeVO.getPermitDate());
				ps.setString(4, tradeVO.getCmpyName());
				ps.setString(5, tradeVO.getCmpyNameEn());
				ps.setString(6, tradeVO.getAddress());
				ps.setString(7, tradeVO.getAddressEn());
				ps.setString(8, tradeVO.getRepresentative());
				ps.setString(9, tradeVO.getTel());
				ps.setString(10, tradeVO.getTaxTel());
				ps.setString(11, tradeVO.getImportAuth());
				ps.setString(12, tradeVO.getExportAuth());
				ps.setString(13, tradeVO.getDataSource());
				ps.setString(14, tradeVO.getDataSourceUrl());
				ps.setTimestamp(15, tradeVO.getUpdateDate());
				ps.addBatch();
				if (count % 2000 == 0) {
					ps.executeBatch();
					ps.clearBatch();
					con.commit();
				}
				count++;
			}
			ps.executeBatch();
			ps.clearBatch();
			con.commit();
			con.close();
			
		} catch (Exception e) {
			con.rollback();
			con.close();
			logger.error("insertToTrade error");
			throw e;
		}
	}

	public List<PubftsVO> getQueryTimes(int nowYear) throws Exception {
		StringBuffer totalSql = new StringBuffer();
		totalSql.append(" select count_date as statistc_date, ");
		totalSql.append(" sum(count_number) as total from pubfts.psysl_counter ");
		totalSql.append(
				" where count_date like '" + nowYear + "%' and count_type in ('esListQuery','cmpyDetailQuery' ");
		totalSql.append(
				" ,'brCmpyDetailQuery','busmDetailQuery','brBusmDetailQuery','lmtdDetailQuery','brLmtdDetailQuery','factDetailQuery')");
		totalSql.append(" group by count_date order by count_date ");
		List<PubftsVO> totalList = jdbcTemplate.query(totalSql.toString(),
				new BeanPropertyRowMapper<PubftsVO>(PubftsVO.class));

		StringBuffer queryTimesSql = new StringBuffer();
		queryTimesSql.append(" select count_date as statistc_date, ");
		queryTimesSql.append(" sum(count_number) as query_times from pubfts.psysl_counter ");
		queryTimesSql.append(" where count_date like '" + nowYear + "%' and count_type = 'esListQuery' ");
		queryTimesSql.append(" group by count_date order by count_date ");
		List<PubftsVO> queryTimesList = jdbcTemplate.query(queryTimesSql.toString(),
				new BeanPropertyRowMapper<PubftsVO>(PubftsVO.class));

		for (int i = 0; i < totalList.size(); i++) {
			PubftsVO pubftsVO = totalList.get(i);
			int queryTimes = queryTimesList.get(i).getQueryTimes();
			pubftsVO.setQueryTimes(queryTimes);
			int detailTimes = pubftsVO.getTotal() - queryTimesList.get(i).getQueryTimes();
			pubftsVO.setDetailTimes(detailTimes);
		}
		return totalList;
	}

	public List<PubftsVO> getIwannaSay(String startDate, String endDate) throws Exception {
		if (startDate.length() != 8 || endDate.length() != 8) {
			throw new Exception();
		}
		StringBuffer queryTimesSql = new StringBuffer();
		queryTimesSql.append(
				" select pk_no as seq_no,create_time,sug_email,sug_cont from psysl_suggestion where create_time >= to_date(?,'yyyymmdd') and create_time <= to_date(?,'yyyymmdd') order by create_time ");
		List<PubftsVO> list = jdbcTemplate.query(queryTimesSql.toString(),
				new BeanPropertyRowMapper<PubftsVO>(PubftsVO.class), new Object[] { startDate, endDate });
		for (PubftsVO vo : list) {
			vo.setSeqNo(vo.getSeqNo() - 2550);
		}
		return list;
	}

}
