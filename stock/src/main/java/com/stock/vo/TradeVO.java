package com.stock.vo;

import java.sql.Timestamp;

public class TradeVO {
	private String banNo; //統一編號
	private Timestamp regDate; //原始登記日期
	private Timestamp permitDate; //核發日期
	private String cmpyName; //廠商中文名稱
	private String cmpyNameEn; //廠商英文名稱
	private String address; //中文營業地址
	private String addressEn; //英文營業地址
	private String representative; //代表人
	private String tel; //電話號碼
	private String taxTel; //傳真號碼
	private String importAuth; //進口資格
	private String exportAuth; //出口資格
	private String dataSource;
	private String dataSourceUrl;
	private Timestamp updateDate;
	
	public String getBanNo() {
		return banNo;
	}
	public void setBanNo(String banNo) {
		this.banNo = banNo;
	}
	public String getCmpyName() {
		return cmpyName;
	}
	public void setCmpyName(String cmpyName) {
		this.cmpyName = cmpyName;
	}
	public String getCmpyNameEn() {
		return cmpyNameEn;
	}
	public void setCmpyNameEn(String cmpyNameEn) {
		this.cmpyNameEn = cmpyNameEn;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getAddressEn() {
		return addressEn;
	}
	public void setAddressEn(String addressEn) {
		this.addressEn = addressEn;
	}
	public String getRepresentative() {
		return representative;
	}
	public void setRepresentative(String representative) {
		this.representative = representative;
	}
	public String getTel() {
		return tel;
	}
	public void setTel(String tel) {
		this.tel = tel;
	}
	public String getTaxTel() {
		return taxTel;
	}
	public void setTaxTel(String taxTel) {
		this.taxTel = taxTel;
	}
	public String getImportAuth() {
		return importAuth;
	}
	public void setImportAuth(String importAuth) {
		this.importAuth = importAuth;
	}
	public String getExportAuth() {
		return exportAuth;
	}
	public void setExportAuth(String exportAuth) {
		this.exportAuth = exportAuth;
	}
	public String getDataSource() {
		return dataSource;
	}
	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}
	public String getDataSourceUrl() {
		return dataSourceUrl;
	}
	public void setDataSourceUrl(String dataSourceUrl) {
		this.dataSourceUrl = dataSourceUrl;
	}
	public Timestamp getRegDate() {
		return regDate;
	}
	public void setRegDate(Timestamp regDate) {
		this.regDate = regDate;
	}
	public Timestamp getPermitDate() {
		return permitDate;
	}
	public void setPermitDate(Timestamp permitDate) {
		this.permitDate = permitDate;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	
	
}
