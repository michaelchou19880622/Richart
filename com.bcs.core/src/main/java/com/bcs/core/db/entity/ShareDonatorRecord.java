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

@Entity
@Table(name = "BCS_SHARE_DONATOR_RECORD", 
indexes = {
        @Index(name = "INDEX_0", columnList = "DONATOR_UID"),
        @Index(name = "INDEX_1", columnList = "CAMPAIGN_ID")
})
public class ShareDonatorRecord extends AbstractBcsEntity{
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name = "SHARE_DONATOR_RECORD_ID")
    private Long shareDonatorRecordId;
	
	@Column(name = "DONATOR_UID", columnDefinition="nvarchar(50)")
	private String donatorUid;

	@Column(name = "BENEFITED_UID", columnDefinition="nvarchar(50)")
	private String benefitedUid;

    @Column(name = "CAMPAIGN_ID")
    private String campaignId;
  
    @Column(name = "SHARE_USER_RECORD_ID", columnDefinition="nvarchar(50)")
    private String shareUserRecordId;
    
    @Column(name = "SHARE_CAMPAIGN_CLICK_TRACING_ID")
    private Long shareCampaignClickTracingId;

	@Column(name = "MODIFY_TIME")
	private Date modifyTime;

	public Long getShareDonatorRecordId() {
		return shareDonatorRecordId;
	}

	public String getDonatorUid() {
		return donatorUid;
	}

	public void setDonatorUid(String donatorUid) {
		this.donatorUid = donatorUid;
	}

	public String getBenefitedUid() {
		return benefitedUid;
	}

	public void setBenefitedUid(String benefitedUid) {
		this.benefitedUid = benefitedUid;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getShareUserRecordId() {
		return shareUserRecordId;
	}

	public void setShareUserRecordId(String shareUserRecordId) {
		this.shareUserRecordId = shareUserRecordId;
	}

	public Long getShareCampaignClickTracingId() {
		return shareCampaignClickTracingId;
	}

	public void setShareCampaignClickTracingId(Long shareCampaignClickTracingId) {
		this.shareCampaignClickTracingId = shareCampaignClickTracingId;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}  
}
