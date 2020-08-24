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
@Table(name = "BCS_TAG_SET_MESSAGE")
public class TagSetMessage extends AbstractBcsEntity {

	private static final long serialVersionUID = 317913090782877365L;

	public static final String TYPE_INTERSECTION = "INTERSECTION";
	public static final String TYPE_UNION = "UNION";
	public static final String TYPE_EXCLUDE = "EXCLUDE";
	
	/* 序列號 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	/* 發送訊息ID */
	@Column(name = "MSG_SEND_MAIN_ID")
	private Long msgSendMainId;

	/* 標籤編號 */
	@Column(name = "TAG_ID")
	private Long tagId;

	/* 建立人員 */
	@Column(name = "SET_TYPE", columnDefinition = "nvarchar(20)")
	private String setType;

	/* 修改時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "SET_TIME")
	private Date setTime;

	public TagSetMessage(Long id, Long msgSendMainId, Long tagId, String setType, Date setTime) {
		this.id = id;
		this.msgSendMainId = msgSendMainId;
		this.tagId = tagId;
		this.setType = setType;
		this.setTime = setTime;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getMsgSendMainId() {
		return msgSendMainId;
	}

	public void setMsgSendMainId(Long msgSendMainId) {
		this.msgSendMainId = msgSendMainId;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String getSetType() {
		return setType;
	}

	public void setSetType(String setType) {
		this.setType = setType;
	}

	public Date getSetTime() {
		return setTime;
	}

	public void setSetTime(Date setTime) {
		this.setTime = setTime;
	}
}
