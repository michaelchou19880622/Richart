package com.bcs.core.db.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.Table;

import com.bcs.core.json.AbstractBcsEntity;

@Entity
@Table(name = "BCS_PUSH_MESSAGE_RECORD")
@NamedNativeQuery(name = "getPushMessageEffects", query =
	"SELECT " + 
		"CASE WHEN A.CREATE_TIME IS NULL THEN B.CREATE_TIME ELSE A.CREATE_TIME END 'CREATE_TIME', " +  
		"CASE WHEN A.PRODUCT IS NULL THEN B.PRODUCT ELSE A.PRODUCT END 'PRODUCT', " + 
		"CASE WHEN A.SUCCESS_COUNT IS NULL THEN 0 ELSE A.SUCCESS_COUNT END 'SUCCESS_COUNT', " + 
		"CASE WHEN B.FAIL_COUNT IS NULL THEN 0 ELSE B.FAIL_COUNT END 'FAIL_COUNT', " + 
		"CASE WHEN A.SEND_TYPE IS NULL THEN B.SEND_TYPE ELSE A.SEND_TYPE END 'SEND_TYPE' " +
	"FROM " + 
		"(" + 
			"(" + 
				"SELECT " + 
					"CREATE_TIME, " + 
					"PRODUCT, " + 
					"COUNT (*) AS SUCCESS_COUNT, " +
					"SEND_TYPE " +
				"FROM " + 
					"BCS_PUSH_MESSAGE_RECORD " + 
				"WHERE " + 
					"MAIN_MESSAGE = 'SUCCESS' " + 
				"GROUP BY " + 
					"CREATE_TIME, " + 
					"PRODUCT, " + 
					"SEND_TYPE " +
			") AS A " + 
			"FULL JOIN " + 
			"(" + 
				"SELECT " + 
					"CREATE_TIME, " + 
					"PRODUCT, " + 
					"COUNT (*) AS FAIL_COUNT, " + 
					"SEND_TYPE " +
				"FROM " + 
					"BCS_PUSH_MESSAGE_RECORD " + 
				"WHERE " + 
					"MAIN_MESSAGE != 'SUCCESS' " + 
				"GROUP BY " + 
					"CREATE_TIME, " + 
					"PRODUCT, " + 
					"SEND_TYPE " +
			") AS B ON A.CREATE_TIME = B.CREATE_TIME " + 
		")" + 
	"WHERE " + 
		"(" + 
			"A.CREATE_TIME >= ?1 " + 
			"OR B.CREATE_TIME >= ?1" + 
		") " + 
		"AND (" + 
			"A.CREATE_TIME < ?2 " + 
			"OR B.CREATE_TIME < ?2" + 
		") " + 
	"ORDER BY " + 
		"CREATE_TIME DESC;"
)
public class PushMessageRecord extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;

	public static final String SOURCE_TYPE_API = "API";
	public static final String SOURCE_TYPE_FTP = "FTP";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "PRODUCT", columnDefinition = "nvarchar(20)")
	private String product;

	@Column(name = "UID", columnDefinition = "nvarchar(50)")
	private String UID;

	@Column(name = "SOURCE_TYPE", columnDefinition = "nvarchar(10)")
	private String sourceType;

	@Column(name = "SOURCE", columnDefinition = "nvarchar(50)")
	private String source;

	@Column(name = "SEND_MESSAGE", columnDefinition = "nvarchar(255)")
	private String sendMessage;

	@Column(name = "SEND_TYPE", columnDefinition = "nvarchar(20)")
	private String sendType;

	@Column(name = "STATUS_CODE", columnDefinition = "nvarchar(3)")
	private String statusCode;

	@Column(name = "MAIN_MESSAGE", columnDefinition = "nvarchar(255)")
	private String mainMessage;

	@Column(name = "DETAIL_MESSAGE", columnDefinition = "nvarchar(255)")
	private String detailMessage;

	@Column(name = "SEND_TIME")
	private Date sendTime;

	@Column(name = "RESERVATION_TIME")
	private Date reservationTime;

	@Column(name = "CREATE_TIME")
	private Date createTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProduct() {
		return product;
	}

	public void setProduct(String product) {
		this.product = product;
	}

	public String getUID() {
		return UID;
	}

	public void setUID(String UID) {
		this.UID = UID;
	}

	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getSendMessage() {
		return sendMessage;
	}

	public void setSendMessage(String sendMessage) {
		this.sendMessage = sendMessage;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMainMessage() {
		return mainMessage;
	}

	public void setMainMessage(String mainMessage) {
		this.mainMessage = mainMessage;
	}

	public String getDetailMessage() {
		return detailMessage;
	}

	public void setDetailMessage(String detailMessage) {
		this.detailMessage = detailMessage;
	}

	public Date getSendTime() {
		return sendTime;
	}

	public void setSendTime(Date sendTime) {
		this.sendTime = sendTime;
	}

	public Date getReservationTime() {
		return reservationTime;
	}

	public void setReservationTime(Date reservationTime) {
		this.reservationTime = reservationTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
}