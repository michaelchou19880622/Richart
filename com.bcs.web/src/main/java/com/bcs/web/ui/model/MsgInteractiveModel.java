package com.bcs.web.ui.model;

import com.bcs.core.json.AbstractBcsEntity;

public class MsgInteractiveModel extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;
	public String type;
	public String status;
	public String keywordInput;
	public String pushDate;
		
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getKeywordInput() {
		return keywordInput;
	}
	
	public void setKeywordInput(String keywordInput) {
		this.keywordInput = keywordInput;
	}
	
	public String getPushDate() {
		return pushDate;
	}
	
	public void setPushDate(String pushDate) {
		this.pushDate = pushDate;
	}
}

