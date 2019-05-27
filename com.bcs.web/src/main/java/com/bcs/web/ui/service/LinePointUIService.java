package com.bcs.web.ui.service;

import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.richart.db.entity.LinePointMain;
import com.bcs.core.richart.db.service.LinePointMainService;
import com.bcs.core.exception.BcsNoticeException;

@Service
public class LinePointUIService {
	/** Logger */
	private static Logger logger = Logger.getLogger(LinePointUIService.class);
	@Autowired
	private LinePointMainService linePointMainService;
	
	
	public LinePointMain linePointMainFindOne(Long id) {
		LinePointMain linePointMain = linePointMainService.findOne(id);
		return linePointMain;
	}
	public List<LinePointMain> linePointMainFindAll(){
		return linePointMainService.findAll();
	}

	
	@Transactional(rollbackFor=Exception.class, timeout = 30)
	public LinePointMain saveLinePointMainFromUI(LinePointMain linePointMain, String adminUserAccount) throws BcsNoticeException{
		logger.info("saveFromUI:" + linePointMain);
		linePointMain.setModifyUser(adminUserAccount);
		linePointMain.setModifyTime(new Date());
		linePointMainService.save(linePointMain);
		return linePointMain;
	}
		
	@Transactional(rollbackFor=Exception.class, timeout = 30)
	public void deleteFromUI(long id, String adminUserAccount, String listType) throws BcsNoticeException {
		logger.info("deleteFromUI:" +id);		
		LinePointMain linePointMain = linePointMainService.findOne(id);
		linePointMainService.delete(linePointMain);
	}	
}
