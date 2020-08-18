package com.bcs.web.ui.model;

import com.bcs.core.json.AbstractBcsEntity;

public class TagModel extends AbstractBcsEntity {

	private static final long serialVersionUID = -5952082784107998354L;

	public static final int STATUS_ACTIVE = 1;
	public static final int STATUS_DELETED = 0;

	private String tagName;

	private String tagDescription;

	private Short validDay;

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagDescription() {
		return tagDescription;
	}

	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}

	public Short getValidDay() {
		return validDay;
	}

	public void setValidDay(Short validDay) {
		this.validDay = validDay;
	}
	
	
}
