package com.bcs.core.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;

@Entity
@Table(name = "richmenu_list")
public class RichmenuListModel extends AbstractBcsEntity{
	private static final long serialVersionUID = 1L;
	
	private Long id;

	private String name;
	
	private Long customeId;
	
	private String richMenuId;
	
	private Date createTime;
	
	private Date modifyTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCustomeId() {
		return customeId;
	}

	public void setCustomeId(Long customeId) {
		this.customeId = customeId;
	}

	public String getRichMenuId() {
		return richMenuId;
	}

	public void setRichMenuId(String richMenuId) {
		this.richMenuId = richMenuId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

}
