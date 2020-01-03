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
	private String phonenumber;
	
	@Column(name = "ID_CARD_NUMBER", columnDefinition = "nvarchar(12)")
	private String id_card_number;
	
	@Column(name = "RESIDENT_ADDRESS", columnDefinition = "nvarchar(200)")
	private String resident_address;
	
	@Column(name = "MAILING_ADDRESS", columnDefinition = "nvarchar(200)")
	private String mailing_address;

	@Column(name = "ID_CARD_COPY_FRONT", columnDefinition = "nvarchar(2083)")
	private String id_card_copy_front;
	
	@Column(name = "ID_CARD_COPY_BACK", columnDefinition = "nvarchar(2083)")
	private String id_card_copy_back;
	
	@Column(name = "E_SIGNATURE", columnDefinition = "nvarchar(2083)")
	private String e_signature;
	

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhonenumber() {
		return phonenumber;
	}

	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}

	public String getId_card_number() {
		return id_card_number;
	}

	public void setId_card_number(String id_card_number) {
		this.id_card_number = id_card_number;
	}

	public String getResident_address() {
		return resident_address;
	}

	public void setResident_address(String resident_address) {
		this.resident_address = resident_address;
	}

	public String getMailing_address() {
		return mailing_address;
	}

	public void setMailing_address(String mailing_address) {
		this.mailing_address = mailing_address;
	}

	public String getId_card_copy_front() {
		return id_card_copy_front;
	}

	public void setId_card_copy_front(String id_card_copy_front) {
		this.id_card_copy_front = id_card_copy_front;
	}

	public String getId_card_copy_back() {
		return id_card_copy_back;
	}

	public void setId_card_copy_back(String id_card_copy_back) {
		this.id_card_copy_back = id_card_copy_back;
	}

	public String getE_signature() {
		return e_signature;
	}

	public void setE_signature(String e_signature) {
		this.e_signature = e_signature;
	}

	@Override
	public String toString() {
		return "WinningLetterRecord [id=" + id + ", winningLetterId=" + winningLetterId + ", uid=" + uid + ", name=" + name + ", phonenumber=" + phonenumber + ", id_card_number=" + id_card_number
				+ ", resident_address=" + resident_address + ", mailing_address=" + mailing_address + ", id_card_copy_front=" + id_card_copy_front + ", id_card_copy_back=" + id_card_copy_back
				+ ", e_signature=" + e_signature + ", recordTime=" + recordTime + "]";
	}
	
}
