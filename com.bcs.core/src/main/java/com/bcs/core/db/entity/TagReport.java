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
@Table(name = "BCS_TAG_REPORT")
public class TagReport extends AbstractBcsEntity {

	private static final long serialVersionUID = 8456959271433108299L;

	/* 序列號 */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	/* 標籤編號 */
	@Column(name = "TAG_ID")
	private Long tagId;

	/* 貼標時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "TAGGING_DATE")
	private Date taggingDate;

	/* 標籤人數 */
	@Column(name = "TOTAL")
	private Long total;

	/* 已綁定(人數) */
	@Column(name = "BINDED")
	private Long binded;

	/* 未綁定(人數) */
	@Column(name = "UNBIND")
	private Long unbind;

	/* 修改時間 */
	@JsonDeserialize(using = CustomDateDeserializer.class)
	@Column(name = "MODIFY_TIME")
	private Date modifyTime;

	public TagReport(Long id, Long tagId, Date taggingDate, Long total, Long binded, Long unbind, Date modifyTime) {
		this.id = id;
		this.tagId = tagId;
		this.taggingDate = taggingDate;
		this.total = total;
		this.binded = binded;
		this.unbind = unbind;
		this.modifyTime = modifyTime;
	}

}
