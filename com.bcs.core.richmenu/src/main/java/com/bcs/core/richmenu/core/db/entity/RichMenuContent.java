package com.bcs.core.richmenu.core.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.bcs.core.json.AbstractBcsEntity;
import com.bcs.core.json.CustomDateDeserializer;

@Entity
@Table(name = "BCS_RICH_MENU_CONTENT")
public class RichMenuContent extends AbstractBcsEntity{
	private static final long serialVersionUID = 1L;

	public static final String STATUS_ACTIVE = "ACTIVE";
	public static final String STATUS_DISABLE = "DISABLE";
	public static final String STATUS_DELETE = "DELETE";
	public static final String STATUS_OPEN = "OPEN";
	public static final String STATUS_CLOSE = "CLOSE";
	public static final String SIZE_FULL = "FULL";
	public static final String SIZE_HALF = "HALF";
	public static final String LEVEL_MAIN = "MAIN";
	public static final String LEVEL_COLUMN = "COLUMN";
	
	@Id
	@Column(name = "RICH_ID", columnDefinition="nvarchar(50)")
	private String richId;
	
	@Column(name = "RICH_MENU_GROUP_ID")
	private Long richMenuGroupId;
	
	@Column(name = "RICH_MENU_ID", columnDefinition="nvarchar(150)")
	private String richMenuId;
	
	@Column(name = "RICH_MENU_NAME", columnDefinition="nvarchar(500)")
	private String richMenuName;

	@Column(name = "RICH_MENU_TITLE", columnDefinition="nvarchar(20)")
	private String richMenuTitle;
	
	@Column(name = "RICH_MENU_SHOW_STATUS", columnDefinition="nvarchar(20)")
	private String richMenuShowStatus;

	@Column(name = "RICH_TYPE", columnDefinition="nvarchar(50)")
	private String richType;

	@Column(name = "RICH_IMAGE_ID", columnDefinition="nvarchar(50)")
	private String richImageId;

	@Column(name = "MODIFY_USER", columnDefinition="nvarchar(50)")
	private String modifyUser;

	@Column(name = "MODIFY_TIME")
	private Date modifyTime;

	@Column(name = "STATUS", columnDefinition="nvarchar(50)")
	private String status;
	
	@Column(name = "LEVEL", columnDefinition="nvarchar(50)")
	private String level;
	
	@Column(name = "MENU_SIZE", columnDefinition="nvarchar(10)")
	private String menuSize;

	public String getRichId() {
		return richId;
	}

	public void setRichId(String richId) {
		this.richId = richId;
	}


	public Long getRichMenuGroupId() {
		return richMenuGroupId;
	}

	public void setRichMenuGroupId(Long richMenuGroupId) {
		this.richMenuGroupId = richMenuGroupId;
	}

	public String getRichMenuId() {
		return richMenuId;
	}

	public void setRichMenuId(String richMenuId) {
		this.richMenuId = richMenuId;
	}
	
	public String getRichMenuName() {
		return richMenuName;
	}

	public void setRichMenuName(String richMenuName) {
		this.richMenuName = richMenuName;
	}

	public String getRichMenuTitle() {
		return richMenuTitle;
	}

	public void setRichMenuTitle(String richMenuTitle) {
		this.richMenuTitle = richMenuTitle;
	}
	
	public String getRichMenuShowStatus() {
		return richMenuShowStatus;
	}

	public void setRichMenuShowStatus(String richMenuShowStatus) {
		this.richMenuShowStatus = richMenuShowStatus;
	}

	public String getRichType() {
		return richType;
	}

	public void setRichType(String richType) {
		this.richType = richType;
	}

	public String getRichImageId() {
		return richImageId;
	}

	public void setRichImageId(String richImageId) {
		this.richImageId = richImageId;
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
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}
	
	public String getMenuSize() {
		return menuSize;
	}

	public void setMenuSize(String menuSize) {
		this.menuSize = menuSize;
	}
}

