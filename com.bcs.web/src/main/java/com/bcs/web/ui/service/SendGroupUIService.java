package com.bcs.web.ui.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bcs.core.db.entity.SendGroup;
import com.bcs.core.db.entity.SendGroupDetail;
import com.bcs.core.db.entity.UserEventSet;
import com.bcs.core.db.repository.SendGroupDetailRepository;
import com.bcs.core.db.service.LineUserService;
import com.bcs.core.db.service.SendGroupService;
import com.bcs.core.db.service.UserEventSetService;
import com.bcs.core.enums.EVENT_TARGET_ACTION_TYPE;
import com.bcs.core.exception.BcsNoticeException;
import com.bcs.core.log.util.SystemLogUtil;
import com.bcs.core.upload.ImportDataFromExcel;
import com.bcs.core.upload.ImportDataFromText;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class SendGroupUIService {

	@Autowired
	private SendGroupService sendGroupService;
	@Autowired
	private SendGroupDetailRepository sendGroupDetailRepository;
	@Autowired
	private ImportDataFromExcel importMidFromExcel;
	@Autowired
	private ImportDataFromText importMidFromText;
	@Autowired
	private LineUserService lineUserService;
	@Autowired
	private UserEventSetService userEventSetService;

	private static int TRANSACTION_TIMEOUT_RETRY_MAX_TIMES = 3;

	private List<String> existMids = new ArrayList<String>();
	private String referenceId;
	private String fileName;
	private Date modifyTime;
	private String modifyUser;
	private int curSaveIndex = 0;
	private int TransactionTimeoutRetry = 0;
	private List<String> list;
	
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.sss");

	/** Logger */
	private static Logger logger = LogManager.getLogger(SendGroupUIService.class);
	
	/**
	 * 新增或修改發送群組
	 * 
	 * @param sendGroup
	 * @param adminUserAccount
	 * @return
	 * @throws BcsNoticeException
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 30)
	public SendGroup saveFromUI(SendGroup sendGroup, String adminUserAccount) throws BcsNoticeException {
		logger.info("saveFromUI : {}", sendGroup);

		Long groupId = sendGroup.getGroupId();
		
		if (groupId != null && groupId < 0) {
			throw new BcsNoticeException("預設群組無法修改");
		}

		String action = (groupId == null ? "Create" : "Edit");

		// Set Modify Admin User
		sendGroup.setModifyUser(adminUserAccount);
		sendGroup.setModifyTime(new Date());

		List<SendGroupDetail> list = sendGroup.getSendGroupDetail();
		logger.debug("list = {}", list);
		
		sendGroup.setSendGroupDetail(new ArrayList<SendGroupDetail>());

		// Save SendGroup First
		sendGroupService.save(sendGroup);

		// Set SendGroupDetail
		if (list != null) {
			for (SendGroupDetail detail : list) {
				detail.setSendGroup(sendGroup);
				sendGroup.getSendGroupDetail().add(detail);
			}
		}

		// Save SendGroup Again With SendGroupDetail
		sendGroupService.save(sendGroup);
		
		sendGroup = sendGroupService.findOne(sendGroup.getGroupId());
		logger.debug("sendGroup = {}", sendGroup);
		
		createSystemLog(action, sendGroup, sendGroup.getModifyUser(), sendGroup.getModifyTime(), sendGroup.getGroupId().toString());
		
		return sendGroup;
	}

	/**
	 * 刪除發送群組
	 * 
	 * @param groupId
	 * @param adminUserAccount
	 * @throws BcsNoticeException
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 30)
	public void deleteFromUI(Long groupId, String adminUserAccount) throws BcsNoticeException {
		logger.debug("deleteFromUI:" + groupId);
		if (groupId < 0) {
			throw new BcsNoticeException("預設群組無法刪除");
		}
		String groupTitle = sendGroupService.findGroupTitleByGroupId(groupId);
		sendGroupService.delete(groupId);
		createSystemLog("Delete", groupTitle, adminUserAccount, new Date(), groupId.toString());
	}

	/**
	 * 新增系統日誌
	 * 
	 * @param action
	 * @param content
	 * @param modifyUser
	 * @param modifyTime
	 */
	private void createSystemLog(String action, Object content, String modifyUser, Date modifyTime, String referenceId) {
		SystemLogUtil.saveLogDebug("SendGroup", action, modifyUser, content, referenceId);
	}

	/**
	 * @param filePart
	 * @param modifyUser
	 * @param modifyTime
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 300000)
	public Map<String, Object> uploadMidSendGroup(MultipartFile filePart, String modifyUser, Date modifyTime) throws Exception {
		logger.info("uploadMidSendGroup");
		
		logger.info("---------- Check Exist Mids ----------");
		long startTime = System.currentTimeMillis();
		logger.info("[ Check Exist Mids ] START TIME : {}", sdf.format(new Date(startTime)));
		
		long endTime = 0;

		logger.info("[ Check Exist Mids ] filePart.getSize() = {}", filePart.getSize());

		fileName = filePart.getOriginalFilename();
		logger.info("[ Check Exist Mids ] getOriginalFilename = {}", fileName);

		String contentType = filePart.getContentType();
		logger.info("[ Check Exist Mids ] getContentType = {}", contentType);

		this.modifyTime = modifyTime;
		this.modifyUser = modifyUser;

		Set<String> mids = null;
		
		if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) || "application/vnd.ms-excel".equals(contentType) || "text/csv".equals(contentType)) {
			mids = importMidFromExcel.importData(filePart.getInputStream());
		} else if ("text/plain".equals(contentType)) {
			mids = importMidFromText.importData(filePart.getInputStream());
		} else {
			logger.info("上傳格式錯誤 - contentType = {}", contentType);
			
			throw new BcsNoticeException("上傳格式錯誤");
		}

		logger.info("[ Check Exist Mids ] mids.size() = {}", mids.size());
		
		// Check Exist Mids
		if (mids != null && mids.size() > 0) {
			list = new ArrayList<String>(mids);

			try {

				List<String> check = new ArrayList<String>();
				
				for (int i = 1; i <= list.size(); i++) {

					check.add(list.get(i - 1));

					// Add into existMids list every 1000 mid checked.
					if (i % 1000 == 0) {
						List<String> midResult = lineUserService.findMidByMidInAndActive(check);
						
						if (midResult != null && midResult.size() > 0) {
							existMids.addAll(midResult);
						}
						
						check.clear();
					}
				}

				// Add left checked mids into existMids list.
				if (check.size() > 0) {
					
					List<String> midResult = lineUserService.findMidByMidInAndActive(check);
					if (midResult != null && midResult.size() > 0) {
						existMids.addAll(midResult);
					}
				}

				endTime = System.currentTimeMillis();
				logger.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} catch (Exception e) {
				logger.info("[ Check Exist Mids ] Exception : {}", e);

				endTime = System.currentTimeMillis();
				logger.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} finally {

				logger.info("[ Check Exist Mids ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
			}

			logger.info("[ Check Exist Mids ] existMids.size() : {}", existMids.size());
			
			logger.info("---------- Save UserEventSet ----------");
			
			// Start Save BCS_USER_EVENT_SET
			startTime = System.currentTimeMillis();
			logger.info("[ Save UserEventSet ] START TIME : {}", sdf.format(new Date(startTime)));
			
			if (existMids != null && existMids.size() > 0) {
				referenceId = UUID.randomUUID().toString().toLowerCase();
				logger.info("[ Save UserEventSet ] referenceId : {}", referenceId);

				try {
					curSaveIndex = 0;

		            int i = 0;
		            List<UserEventSet> userEventSetList = new ArrayList<>();
		            for (String mid : existMids) {
						UserEventSet userEventSet = new UserEventSet();
						userEventSet.setTarget(EVENT_TARGET_ACTION_TYPE.EVENT_SEND_GROUP.toString());
						userEventSet.setAction(EVENT_TARGET_ACTION_TYPE.ACTION_UPLOAD_MID.toString());
						userEventSet.setReferenceId(referenceId);
						userEventSet.setMid(mid);
						userEventSet.setContent(fileName);
						userEventSet.setSetTime(modifyTime);
						userEventSet.setModifyUser(modifyUser);

		                /* 效能優化 ： 組裝userEventSetList , 一次儲存 */
		                userEventSetList.add(userEventSet);
		                i++;
		                /* 每十萬筆處理一次 */
		                if (i % 100000 == 0) {
		                    logger.info("userEventSetList size:" + userEventSetList.size());
		                	userEventSetService.save(userEventSetList);
		                	userEventSetList.clear();
		                }     						
					}
		            /* Update userEventSet */
		            if (!userEventSetList.isEmpty()) {
		                logger.info("userEventSetList size:" + userEventSetList.size());
		            	userEventSetService.save(userEventSetList);
		            	userEventSetList.clear();
		            }      					

					endTime = System.currentTimeMillis();
					logger.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					logger.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));

				} catch (Exception e) {
					endTime = System.currentTimeMillis();
					logger.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					logger.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
					throw new Exception("TimeOut");			
				}

				Map<String, Object> result = new HashMap<String, Object>();

				result.put("referenceId", referenceId);
				result.put("count", existMids.size());
				logger.info("result = {}", result);

				existMids.clear();
				return result;
			} else {
				throw new BcsNoticeException("上傳名單無資料或查無對應UID資料");
			}
		} else {
			throw new BcsNoticeException("上傳名單無資料或查無對應UID資料");
		}
	}
	

	/**
	 * @param filePart
	 * @param modifyUser
	 * @param modifyTime
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 300000)
	public Map<String, Object> uploadRichmenuMidSendGroup(MultipartFile filePart, String modifyUser, Date modifyTime) throws Exception {
		logger.info("uploadRichmenuMidSendGroup");
		
		logger.info("---------- Check Exist Mids ----------");
		long startTime = System.currentTimeMillis();
		logger.info("[ Check Exist Mids ] START TIME : {}", sdf.format(new Date(startTime)));
		
		long endTime = 0;

		logger.info("[ Check Exist Mids ] filePart.getSize() = {}", filePart.getSize());

		fileName = filePart.getOriginalFilename();
		logger.info("[ Check Exist Mids ] getOriginalFilename = {}", fileName);

		String contentType = filePart.getContentType();
		logger.info("[ Check Exist Mids ] getContentType = {}", contentType);

		this.modifyTime = modifyTime;
		this.modifyUser = modifyUser;

		Set<String> mids = null;
		
		if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) || "application/vnd.ms-excel".equals(contentType) || "text/csv".equals(contentType)) {
			mids = importMidFromExcel.importData(filePart.getInputStream());
		} else if ("text/plain".equals(contentType)) {
			mids = importMidFromText.importData(filePart.getInputStream());
		} else {
			logger.info("上傳格式錯誤 - contentType = {}", contentType);
			
			throw new BcsNoticeException("上傳格式錯誤");
		}

		logger.info("[ Check Exist Mids ] mids.size() = {}", mids.size());
		
		// Check Exist Mids
		if (mids != null && mids.size() > 0) {
			list = new ArrayList<String>(mids);

			try {

				List<String> check = new ArrayList<String>();
				
				for (int i = 1; i <= list.size(); i++) {

					check.add(list.get(i - 1));

					// Add into existMids list every 1000 mid checked.
					if (i % 1000 == 0) {
						List<String> midResult = lineUserService.findMidByMidInAndActive(check);
						
						if (midResult != null && midResult.size() > 0) {
							existMids.addAll(midResult);
						}
						
						check.clear();
					}
				}

				// Add left checked mids into existMids list.
				if (check.size() > 0) {
					
					List<String> midResult = lineUserService.findMidByMidInAndActive(check);
					if (midResult != null && midResult.size() > 0) {
						existMids.addAll(midResult);
					}
				}

				endTime = System.currentTimeMillis();
				logger.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} catch (Exception e) {
				logger.info("[ Check Exist Mids ] Exception : {}", e);

				endTime = System.currentTimeMillis();
				logger.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} finally {

				logger.info("[ Check Exist Mids ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
			}

			logger.info("[ Check Exist Mids ] existMids.size() : {}", existMids.size());
			
			logger.info("---------- Save UserEventSet ----------");
			
			// Start Save BCS_USER_EVENT_SET
			startTime = System.currentTimeMillis();
			logger.info("[ Save UserEventSet ] START TIME : {}", sdf.format(new Date(startTime)));
			
			if (existMids != null && existMids.size() > 0) {
				referenceId = UUID.randomUUID().toString().toLowerCase();
				logger.info("[ Save UserEventSet ] referenceId : {}", referenceId);

				try {
					curSaveIndex = 0;

		            int i = 0;
		            List<UserEventSet> userEventSetList = new ArrayList<>();
		            for (String mid : existMids) {

						UserEventSet userEventSet = new UserEventSet();
						userEventSet.setTarget(EVENT_TARGET_ACTION_TYPE.TARGET_RICHMENU_SEND_GROUP.toString());
						userEventSet.setAction(EVENT_TARGET_ACTION_TYPE.ACTION_UPLOAD_RICHMENU_MID.toString());
						userEventSet.setReferenceId(referenceId);
						userEventSet.setMid(mid);
						userEventSet.setContent(fileName);
						userEventSet.setSetTime(modifyTime);
						userEventSet.setModifyUser(modifyUser);

//						logger.info("[ Save UserEventSet ] userEventSet : {}", userEventSet);
		                /* 效能優化 ： 組裝userEventSetList , 一次儲存 */
		                userEventSetList.add(userEventSet);
		                i++;
		                /* 每十萬筆處理一次 */
		                if (i % 100000 == 0) {
		                    logger.info("userEventSetList size:" + userEventSetList.size());
		                	userEventSetService.save(userEventSetList);
		                	userEventSetList.clear();
		                }     	
					}
		            /* Update userEventSet */
		            if (!userEventSetList.isEmpty()) {
		                logger.info("userEventSetList size:" + userEventSetList.size());
		            	userEventSetService.save(userEventSetList);
		            	userEventSetList.clear();
		            }      					

					endTime = System.currentTimeMillis();
					logger.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					logger.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));

				} catch (Exception e) {
					endTime = System.currentTimeMillis();
					logger.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					logger.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
					throw new Exception("TimeOut");					
				}

				Map<String, Object> result = new HashMap<String, Object>();

				result.put("referenceId", referenceId);
				result.put("count", existMids.size());
				logger.info("result = {}", result);

				existMids.clear();
				return result;
			} else {
				throw new BcsNoticeException("上傳名單無資料或查無對應UID資料");
			}
		} else {
			throw new BcsNoticeException("上傳名單無資料或查無對應UID資料");
		}
	}
}
