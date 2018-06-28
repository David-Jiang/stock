package com.stock.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

public class DisbandVO {
	private String idNo;
	private String rcvNo;
	private BigDecimal recvSeq;
	private String rcvOrgn;
	private String rcvOrgnAddr;
	private String isSendArrive;
	private Timestamp updateDate;
	private String updateUser;
	
	public String getIdNo() {
		return idNo;
	}
	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}
	public BigDecimal getRecvSeq() {
		return recvSeq;
	}
	public void setRecvSeq(BigDecimal recvSeq) {
		this.recvSeq = recvSeq;
	}
	public String getRcvOrgn() {
		return rcvOrgn;
	}
	public void setRcvOrgn(String rcvOrgn) {
		this.rcvOrgn = rcvOrgn;
	}
	public String getRcvOrgnAddr() {
		return rcvOrgnAddr;
	}
	public void setRcvOrgnAddr(String rcvOrgnAddr) {
		this.rcvOrgnAddr = rcvOrgnAddr;
	}
	public Timestamp getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Timestamp updateDate) {
		this.updateDate = updateDate;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getRcvNo() {
		return rcvNo;
	}
	public void setRcvNo(String rcvNo) {
		this.rcvNo = rcvNo;
	}
	public String getIsSendArrive() {
		return isSendArrive;
	}
	public void setIsSendArrive(String isSendArrive) {
		this.isSendArrive = isSendArrive;
	}
	
	
}
