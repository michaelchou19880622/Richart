package com.bcs.web.ui.model;

import com.bcs.core.json.AbstractBcsEntity;

public class ResponseModel extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;

	private String message;

	public ResponseModel(String str) {
		this.message = str;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
