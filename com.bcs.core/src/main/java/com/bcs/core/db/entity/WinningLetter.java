//package com.bcs.core.db.entity;
//
//import java.util.Date;
//
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Index;
//import javax.persistence.Table;
//
//import com.bcs.core.json.AbstractBcsEntity;
//import com.bcs.core.json.CustomDateDeserializer;
//import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
//
////@Entity
////@Table(name = "BCS_WINNING_LETTER", indexes = { @Index(name = "INDEX_0", columnList = "ID") })
//public class WinningLetter extends AbstractBcsEntity {
//	private static final long serialVersionUID = 1L;
//	
//    public static final String STATUS_ACTIVE = "Active";
//    public static final String STATUS_INACTIVE = "Inactive";
//    public static final String STATUS_DELETED = "Deleted";
//
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
//	@Column(name = "ID")
//	private Long id;
//
//	@Column(name = "NAME", columnDefinition = "nvarchar(50)")
//	private String name;
//
//	@JsonDeserialize(using = CustomDateDeserializer.class)
//	@Column(name = "START_TIME")
//	private Date startTime;
//
//	@JsonDeserialize(using = CustomDateDeserializer.class)
//	@Column(name = "END_TIME")
//	private Date endTime;
//
//	@Column(name = "GIFT", columnDefinition = "nvarchar(100)")
//	private String gift;
//
//	@Column(name = "STATUS", columnDefinition = "nvarchar(10)")
//	private String status;
//
//	@Column(name = "CREATE_USER", columnDefinition = "nvarchar(50)")
//	private String createUser;
//
//	@JsonDeserialize(using = CustomDateDeserializer.class)
//	@Column(name = "CREATE_TIME")
//	private Date createTime;
//
//	@Column(name = "MODIFY_USER", columnDefinition = "nvarchar(50)")
//	private String modifyUser;
//
//	@JsonDeserialize(using = CustomDateDeserializer.class)
//	@Column(name = "MODIFY_TIME")
//	private Date modifyTime;
//
//	public Long getId() {
//		return id;
//	}
//
//	public void setId(Long id) {
//		this.id = id;
//	}
//
//	public String getName() {
//		return name;
//	}
//
//	public void setName(String name) {
//		this.name = name;
//	}
//
//	public Date getStartTime() {
//		return startTime;
//	}
//
//	public void setStartTime(Date startTime) {
//		this.startTime = startTime;
//	}
//
//	public Date getEndTime() {
//		return endTime;
//	}
//
//	public void setEndTime(Date endTime) {
//		this.endTime = endTime;
//	}
//
//	public String getGift() {
//		return gift;
//	}
//
//	public void setGift(String gift) {
//		this.gift = gift;
//	}
//
//	public String getStatus() {
//		return status;
//	}
//
//	public void setStatus(String status) {
//		this.status = status;
//	}
//
//	public String getCreateUser() {
//		return createUser;
//	}
//
//	public void setCreateUser(String createUser) {
//		this.createUser = createUser;
//	}
//
//	public Date getCreateTime() {
//		return createTime;
//	}
//
//	public void setCreateTime(Date createTime) {
//		this.createTime = createTime;
//	}
//
//	public String getModifyUser() {
//		return modifyUser;
//	}
//
//	public void setModifyUser(String modifyUser) {
//		this.modifyUser = modifyUser;
//	}
//
//	public Date getModifyTime() {
//		return modifyTime;
//	}
//
//	public void setModifyTime(Date modifyTime) {
//		this.modifyTime = modifyTime;
//	}
//
//	@Override
//	public String toString() {
//		return "WinningLetter [id=" + id + ", name=" + name + ", startTime=" + startTime + ", endTime=" + endTime + ", gift=" + gift + ", status=" + status + ", createUser=" + createUser
//				+ ", createTime=" + createTime + ", modifyUser=" + modifyUser + ", modifyTime=" + modifyTime + "]";
//	}
//	
//	
//}
