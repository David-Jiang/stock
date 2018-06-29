package com.stock.vo;

public class SecuritiesVO {
	private String transactionDate;
	private int totalAmount;
	private int investAmount;
	private int nativeAmount;
	private int foreignAmount;
	
	public String getTransactionDate() {
		return transactionDate;
	}
	public void setTransactionDate(String transactionDate) {
		this.transactionDate = transactionDate;
	}
	public int getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(int totalAmount) {
		this.totalAmount = totalAmount;
	}
	public int getInvestAmount() {
		return investAmount;
	}
	public void setInvestAmount(int investAmount) {
		this.investAmount = investAmount;
	}
	public int getNativeAmount() {
		return nativeAmount;
	}
	public void setNativeAmount(int nativeAmount) {
		this.nativeAmount = nativeAmount;
	}
	public int getForeignAmount() {
		return foreignAmount;
	}
	public void setForeignAmount(int foreignAmount) {
		this.foreignAmount = foreignAmount;
	}
	
	
}
