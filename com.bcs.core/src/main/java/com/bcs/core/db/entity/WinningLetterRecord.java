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
import com.bcs.core.json.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "BCS_WINNING_LETTER_RECORD_TEST", indexes = { @Index(name = "INDEX_0", columnList = "ID") })
public class WinningLetterRecord extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "WINNING_LETTER_ID")
	private Long winningLetterId;

	@Column(name = "UID", columnDefinition = "nvarchar(50)")
	private String uid;

	@Column(name = "NAME", columnDefinition = "nvarchar(10)")
	private String name;

	@Column(name = "PHONENUMBER", columnDefinition = "nvarchar(32)")
	private String phonenumber;
	
	@Column(name = "ID_CARD_NUMBER", columnDefinition = "nvarchar(12)")
	private String id_card_number;
	
	@Column(name = "RESIDENT_ADDRESS", columnDefinition = "nvarchar(200)")
	private String resident_address;
	
	@Column(name = "MAILING_ADDRESS", columnDefinition = "nvarchar(200)")
	private String mailing_address;

	@Column(name = "ID_CARD_COPY_FRONT", columnDefinition = "varbinary(max)")
	private byte[] id_card_copy_front;
	
	@Column(name = "ID_CARD_COPY_BACK", columnDefinition = "varbinary(max)")
	private byte[] id_card_copy_back;
	
	@Column(name = "E_SIGNATURE", columnDefinition = "varbinary(max)")
	private byte[] e_signature;

	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "RECORD_TIME")
	private Date recordTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getWinningLetterId() {
		return winningLetterId;
	}

	public void setWinningLetterId(Long winningLetterId) {
		this.winningLetterId = winningLetterId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Date getRecordTime() {
		return recordTime;
	}

	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}

	@Override
	public String toString() {
		return "WinningLetterRecord [id=" + id + ", winningLetterId=" + winningLetterId + ", uid=" + uid + ", recordTime=" + recordTime + "]";
	}
}
