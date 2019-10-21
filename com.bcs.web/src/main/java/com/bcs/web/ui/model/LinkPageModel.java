package com.bcs.web.ui.model;

import com.bcs.core.json.AbstractBcsEntity;

public class LinkPageModel extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;

	public String flag;
	public String page;
		
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}
}

