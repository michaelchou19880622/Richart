package com.bcs.core.bot.pnp.model;

import java.util.Date;

import com.bcs.core.json.AbstractBcsEntity;

public class FileHeaderModel extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;
	
	private String product;
	private String messageSendType;
	private Date scheduledTime;
	
	public String getProduct() {
		return product;
	}
	public void setProduct(String product) {
		this.product = product;
	}
	
	public String getMessageSendType() {
		return messageSendType;
	}
	public void setMessageType(String messageSendType) {
		this.messageSendType = messageSendType;
	}
	
	public Date getScheduledTime() {
		return scheduledTime;
	}
	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}
}