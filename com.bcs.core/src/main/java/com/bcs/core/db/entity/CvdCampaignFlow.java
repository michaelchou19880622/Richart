package com.bcs.core.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;

@Entity
@Table(name = "CVD_CAMPAIGN_FLOW", indexes = { @Index(name = "INDEX_ID", columnList = "ID") })
public class CvdCampaignFlow extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;

	public static final String STATUS_INPROGRESS = "INPROGRESS";
	public static final String STATUS_FINISHED = "FINISHED";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "UID", columnDefinition = "nvarchar(50)")
	private String uid;

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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "SpringTreeCampaignFlow [id=" + id + ", uid=" + uid + ", status=" + status + "]";
	}
}
