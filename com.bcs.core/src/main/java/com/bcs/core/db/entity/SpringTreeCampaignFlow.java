package com.bcs.core.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;

@Entity
@Table(name = "SPRINGTREE_CAMPAIGN_FLOW", indexes = { @Index(name = "INDEX_ID", columnList = "ID") })
public class SpringTreeCampaignFlow extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;

	public static final String STATUS_INPROGRESS = "INPROGRESS";
	public static final String STATUS_FINISHED = "FINISHED";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "UID", columnDefinition = "nvarchar(50)")
	private String uid;

	@Column(name = "CREATE_TIME")
	private Date createTime;

	@Column(name = "MODIFY_TIME")
	private Date modifyTime;

	@Column(name = "STATUS", columnDefinition = "nvarchar(20)")
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "SpringTreeCampaignFlow [id=" + id + ", uid=" + uid + ", createTime=" + createTime + ", modifyTime=" + modifyTime + ", status=" + status + "]";
	}

}
