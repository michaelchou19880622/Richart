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
@Table(name = "BCS_TAG_EXPIRED_CHECK")
public class TagExpiredCheck extends AbstractBcsEntity {

	private static final long serialVersionUID = 98404802570601762L;

	/* 序列號 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	/* 標籤編號 */
	@Column(name = "TAG_ID")
	private Long tagId;

	/* LINE UID */
	@Column(name = "UID", columnDefinition = "nvarchar(50)")
	private String uid;

	/* 貼標時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "TAGGING_DATE")
	private Date taggingDate;

	/* 逾期時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "EXPIRED_DATE")
	private Date expiredDate;

	public TagExpiredCheck(Long id, Long tagId, String uid, Date taggingDate, Date expiredDate) {
		this.id = id;
		this.tagId = tagId;
		this.uid = uid;
		this.taggingDate = taggingDate;
		this.expiredDate = expiredDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getTaggingDate() {
		return taggingDate;
	}

	public void setTaggingDate(Date taggingDate) {
		this.taggingDate = taggingDate;
	}

	public Date getExpiredDate() {
		return expiredDate;
	}

	public void setExpiredDate(Date expiredDate) {
		this.expiredDate = expiredDate;
	}
}
