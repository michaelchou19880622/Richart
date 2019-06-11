package com.bcs.core.richart.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.json.JSONArray;

import com.bcs.core.json.AbstractBcsEntity;
import com.bcs.core.utils.ObjectUtil;

@Entity
@Table(name = "BCS_LINE_POINT_SCHEDULED_DETAIL")
public class LinePointScheduledDetail extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name = "DETAIL_ID")
	private Long detailId;

	@Column(name = "LINE_POINT_MAIN_ID")
	private Long linePointMainId;

	// Post Model
	@Column(name = "UID", columnDefinition="varchar(50)")
	private String uid;

	public Long getDetailId() {
		return detailId;
	}

	public Long getLinePointMainId() {
		return linePointMainId;
	}

	public void setLinePointMainId(Long linePointMainId) {
		this.linePointMainId = linePointMainId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
}
