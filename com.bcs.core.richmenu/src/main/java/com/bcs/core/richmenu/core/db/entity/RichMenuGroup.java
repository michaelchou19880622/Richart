package com.bcs.core.richmenu.core.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.bcs.core.json.AbstractBcsEntity;
import com.bcs.core.json.CustomDateDeserializer;

@Entity
@Table(name = "BCS_RICH_MENU_GROUP")
public class RichMenuGroup extends AbstractBcsEntity{
	private static final long serialVersionUID = 1L;

	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_DELETE = "DELETE";
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "RICH_MENU_GROUP_ID")
	private Long richMenuGroupId;

	@Column(name = "RICH_MENU_GROUP_NAME", columnDefinition="nvarchar(500)")
	private String richMenuGroupName;

	@Column(name = "MODIFY_USER", columnDefinition="nvarchar(50)")
	private String modifyUser;

	@Column(name = "MODIFY_TIME")
	private Date modifyTime;

	@Column(name = "STATUS", columnDefinition="nvarchar(50)")
	private String status;

	public Long getRichMenuGroupId() {
		return richMenuGroupId;
	}

	public String getRichMenuGroupName() {
		return richMenuGroupName;
	}

	public void setRichMenuGroupName(String richMenuGroupName) {
		this.richMenuGroupName = richMenuGroupName;
	}

	public String getModifyUser() {
		return modifyUser;
	}

	public void setModifyUser(String modifyUser) {
		this.modifyUser = modifyUser;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
