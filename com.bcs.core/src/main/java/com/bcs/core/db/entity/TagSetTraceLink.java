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
@Table(name = "BCS_TAG_SET_TRACELINK")
public class TagSetTraceLink extends AbstractBcsEntity {

	private static final long serialVersionUID = -2359624971477198057L;

	/* 序列號 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	/* 追蹤連結ID */
	@Column(name = "LINK_ID", columnDefinition = "nvarchar(50)")
	private String linkId;

	/* 標籤編號 */
	@Column(name = "TAG_ID")
	private Long tagId;

	/* 修改時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "SET_TIME")
	private Date setTime;

	public TagSetTraceLink(Long id, String linkId, Long tagId, Date setTime) {
		this.id = id;
		this.linkId = linkId;
		this.tagId = tagId;
		this.setTime = setTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLinkId() {
		return linkId;
	}

	public void setLinkId(String linkId) {
		this.linkId = linkId;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public Date getSetTime() {
		return setTime;
	}

	public void setSetTime(Date setTime) {
		this.setTime = setTime;
	}
}
