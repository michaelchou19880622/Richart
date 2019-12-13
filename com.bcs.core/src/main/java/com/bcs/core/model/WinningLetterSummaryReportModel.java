package com.bcs.core.model;

import java.util.Date;

import com.bcs.core.json.AbstractBcsEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WinningLetterSummaryReportModel extends AbstractBcsEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	
	private String status;
	
	private String replycount;
	
	private String url;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date expiredTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createTime;

	private String createUser;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date modifyTime;

	private String modifyUser;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReplycount() {
		return replycount;
	}

	public void setReplycount(String replycount) {
		this.replycount = replycount;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getExpiredTime() {
		return expiredTime;
	}

	public void setExpiredTime(Date expiredTime) {
		this.expiredTime = expiredTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	@Override
	public String toString() {
		return "WinningLetterSummaryReportModel [name=" + name + ", status=" + status + ", replycount=" + replycount + ", url=" + url + ", expiredTime=" + expiredTime + ", createTime=" + createTime
				+ ", createUser=" + createUser + ", modifyTime=" + modifyTime + ", modifyUser=" + modifyUser + "]";
	}
	
	
}
