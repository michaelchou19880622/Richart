package com.bcs.core.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Index;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;
import com.bcs.core.json.CustomDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@Entity
@Table(name = "BCS_WINNING_LETTER_RECORD", indexes = { @Index(name = "INDEX_0", columnList = "ID") })
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
	private String phoneNumber;
	
	@Column(name = "ID_CARD_NUMBER", columnDefinition = "nvarchar(12)")
	private String idCardNumber;
	
	@Column(name = "RESIDENT_ADDRESS", columnDefinition = "nvarchar(200)")
	private String residentAddress;
	
	@Column(name = "MAILING_ADDRESS", columnDefinition = "nvarchar(200)")
	private String mailingAddress;

	@Column(name = "ID_CARD_COPY_FRONT", columnDefinition = "nvarchar(2083)")
	private String idCardCopyFront;
	
	@Column(name = "ID_CARD_COPY_BACK", columnDefinition = "nvarchar(2083)")
	private String idCardCopyBack;
	
	@Column(name = "E_SIGNATURE", columnDefinition = "nvarchar(2083)")
	private String eSignature;
	

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


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPhoneNumber() {
		return phoneNumber;
	}


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}


	public String getIdCardNumber() {
		return idCardNumber;
	}


	public void setIdCardNumber(String idCardNumber) {
		this.idCardNumber = idCardNumber;
	}


	public String getResidentAddress() {
		return residentAddress;
	}


	public void setResidentAddress(String residentAddress) {
		this.residentAddress = residentAddress;
	}


	public String getMailingAddress() {
		return mailingAddress;
	}


	public void setMailingAddress(String mailingAddress) {
		this.mailingAddress = mailingAddress;
	}


	public String getIdCardCopyFront() {
		return idCardCopyFront;
	}


	public void setIdCardCopyFront(String idCardCopyFront) {
		this.idCardCopyFront = idCardCopyFront;
	}


	public String getIdCardCopyBack() {
		return idCardCopyBack;
	}


	public void setIdCardCopyBack(String idCardCopyBack) {
		this.idCardCopyBack = idCardCopyBack;
	}


	public String geteSignature() {
		return eSignature;
	}


	public void seteSignature(String eSignature) {
		this.eSignature = eSignature;
	}


	public Date getRecordTime() {
		return recordTime;
	}


	public void setRecordTime(Date recordTime) {
		this.recordTime = recordTime;
	}


	@Override
	public String toString() {
		return "WinningLetterRecord [id=" + id + ", winningLetterId=" + winningLetterId + ", uid=" + uid + ", name=" + name + ", phoneNumber=" + phoneNumber + ", idCardNumber=" + idCardNumber
				+ ", residentAddress=" + residentAddress + ", mailingAddress=" + mailingAddress + ", idCardCopyFront=" + idCardCopyFront + ", idCardCopyBack=" + idCardCopyBack + ", eSignature="
				+ eSignature + ", recordTime=" + recordTime + "]";
	}

	
}
