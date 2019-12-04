package com.bcs.core.richart.api.model;

import java.util.Date;

import com.bcs.core.json.AbstractBcsEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

public class WinningLetterModel extends AbstractBcsEntity {

	private static final long serialVersionUID = 1L;

	private String name;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date startTime;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date endTime;

	private String gift;

	private String status;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getGift() {
		return gift;
	}

	public void setGift(String gift) {
		this.gift = gift;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "WinningLetterModel [name=" + name + ", startTime=" + startTime + ", endTime=" + endTime + ", gift=" + gift + ", status=" + status + "]";
	}

}
