package com.bcs.core.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;

import lombok.Data;

@Data
@Entity
@Table(name = "richmenu_list")
public class RichMenuList extends AbstractBcsEntity{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;
	
	@Column(name = "custom_id")
	private Long customeId;
	
	@Column(name = "richmenu_id")
	private String richMenuId;
	
	@Column(name = "create_time")
	private Date createTime;
	
	@Column(name = "modify_time")
	private Date modifyTime;
	
	public RichMenuList(){
		
	}

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
