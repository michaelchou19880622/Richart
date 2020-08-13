package com.bcs.core.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;
import com.bcs.core.json.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "BCS_TAG")
public class Tag extends AbstractBcsEntity {

	private static final long serialVersionUID = -5952082784107998354L;

	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_DELETED = 0;

	/* 標籤編號 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	/* 標籤名稱 */
	@Column(name = "TAG_NAME", columnDefinition = "nvarchar(30)")
	private String tagName;

	/* 標籤說明 */
	@Column(name = "TAG_DESCRIPTION", columnDefinition = "nvarchar(210)")
	private String tagDescription;

	/* 建立時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "CREATE_TIME")
	private Date createTime;

	/* 建立人員 */
	@Column(name = "CREATE_USER", columnDefinition = "nvarchar(50)")
	private String createUser;

	/* 修改時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "MODIFY_TIME")
	private Date modifyTime;

	/* 修改人員 */
	@Column(name = "MODIFY_USER", columnDefinition = "nvarchar(50)")
	private String modifyUser;

	/* 狀態 */
	@Column(name = "STATUS", columnDefinition = "tinyint", length = 1)
	private Boolean status;

	/* 標籤時效(生命週期) */
	@Column(name = "VALID_DAY", columnDefinition = "smallint")
	private Short validDay;

	public Tag(Long id, String tagName, String tagDescription, Date createTime, String createUser, Date modifyTime, String modifyUser, Boolean status, Short validDay) {
		this.id = id;
		this.tagName = tagName;
		this.tagDescription = tagDescription;
		this.createTime = createTime;
		this.createUser = createUser;
		this.modifyTime = modifyTime;
		this.modifyUser = modifyUser;
		this.status = status;
		this.validDay = validDay;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagDescription() {
		return tagDescription;
	}

	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
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

	public Boolean getStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	public Short getValidDay() {
		return validDay;
	}

	public void setValidDay(Short validDay) {
		this.validDay = validDay;
	}
}
