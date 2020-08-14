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
@Table(name = "BCS_TAG_SET_KEYWORD")
public class TagSetKeyword extends AbstractBcsEntity {

	private static final long serialVersionUID = 4260031778674208289L;

	/* 序列號 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	/* 自動回覆關鍵字ID */
	@Column(name = "MSG_INTERACTIVE_ID")
	private Long msgInteractiveId;

	/* 標籤編號 */
	@Column(name = "TAG_ID")
	private Long tagId;

	/* 修改時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "SET_TIME")
	private Date setTime;

	public TagSetKeyword(Long id, Long msgInteractiveId, Long tagId, Date setTime) {
		this.id = id;
		this.msgInteractiveId = msgInteractiveId;
		this.tagId = tagId;
		this.setTime = setTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMsgInteractiveId() {
		return msgInteractiveId;
	}

	public void setMsgInteractiveId(Long msgInteractiveId) {
		this.msgInteractiveId = msgInteractiveId;
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
