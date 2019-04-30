package com.bcs.core.bot.pnp.model;

import java.util.Date;
import java.util.List;

import com.bcs.core.json.AbstractBcsEntity;

public class FtpTaskModel extends AbstractBcsEntity {
	private static final long serialVersionUID = 1L;
	
	private String fileName;
	private FileHeaderModel fileHead;
	private List<String> lineMessageObjects;
	private Date timestamp;
	private Boolean isScheduled;
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public FileHeaderModel getFileHead() {
		return fileHead;
	}
	public void setFileHead(FileHeaderModel fileHead) {
		this.fileHead = fileHead;
	}
	
	public List<String> getLineMessageObjects() {
		return lineMessageObjects;
	}
	public void setLineMessageObjects(List<String> lineMessageObjects) {
		this.lineMessageObjects = lineMessageObjects;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public Boolean getIsScheduled() {
		return isScheduled;
	}
	public void setIsScheduled(Boolean isScheduled) {
		this.isScheduled = isScheduled;
	}
}