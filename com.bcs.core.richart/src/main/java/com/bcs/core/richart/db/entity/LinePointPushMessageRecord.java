package com.bcs.core.richart.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;

@Entity
@Table(name = "BCS_LINE_POINT_PUSH_MESSAGE_RECORD")
public class LinePointPushMessageRecord extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;
	
	public static final String STATUS_WAIT = "WAIT";
	public static final String SYSTEM_FAIL = "FAIL";
	public static final String SYSTEM_SUCCESS = "SUCCESS";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "EVENT_ID")
	private Long eventId;
	
	@Column(name = "MEMBER_ID", columnDefinition="nvarchar(50)")
	private String memberId;

	@Column(name = "ORDER_KEY", columnDefinition="varchar(50)")
	private String orderKey;

	@Column(name = "AMOUNT")
	private Long amount;

	@Column(name = "STATUS", columnDefinition="nvarchar(20)")
	private String status;
	
	@Column(name = "SEND_TIME")
	private Date sendTime;
	
	@Column(name = "CREATE_TIME")
	private Date createTime;

	public Long getAmount() {
		return amount;
	}
	public void setAmount(Long amount) {
		this.amount = amount;
	}
	public Long getEventId() {
		return eventId;
	}
	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Date getSendTime() {
		return sendTime;
	}
	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public String getMemberId() {
		return memberId;
	}
	
	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
	public String getOrderKey() {
		return orderKey;
	}
	
	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Date getCreateTime() {
		return createTime;
	}
	
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}