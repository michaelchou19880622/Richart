package com.bcs.web.ui.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.bcs.core.db.entity.ContentCoupon;
import com.bcs.core.db.entity.ContentGame;
import com.bcs.core.db.entity.ContentRewardCard;
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

