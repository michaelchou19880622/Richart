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
		log.info("saveFromUI : {}", sendGroup);

		Long groupId = sendGroup.getGroupId();
		
		if (groupId != null && groupId < 0) {
			throw new BcsNoticeException("預設群組無法修改");
		}

		String action = (groupId == null ? "Create" : "Edit");

		// Set Modify Admin User
		sendGroup.setModifyUser(adminUserAccount);
		sendGroup.setModifyTime(new Date());

		List<SendGroupDetail> list = sendGroup.getSendGroupDetail();
		log.debug("list = {}", list);
		
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
		log.debug("sendGroup = {}", sendGroup);
		
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
		log.debug("deleteFromUI:" + groupId);
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
		log.info("uploadMidSendGroup");
		
		log.info("---------- Check Exist Mids ----------");
		long startTime = System.currentTimeMillis();
		log.info("[ Check Exist Mids ] START TIME : {}", sdf.format(new Date(startTime)));
		
		long endTime = 0;

		log.info("[ Check Exist Mids ] filePart.getSize() = {}", filePart.getSize());

		fileName = filePart.getOriginalFilename();
		log.info("[ Check Exist Mids ] getOriginalFilename = {}", fileName);

		String contentType = filePart.getContentType();
		log.info("[ Check Exist Mids ] getContentType = {}", contentType);

		this.modifyTime = modifyTime;
		this.modifyUser = modifyUser;

		Set<String> mids = null;
		
		if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) || "application/vnd.ms-excel".equals(contentType)) {
			mids = importMidFromExcel.importData(filePart.getInputStream());
		} else if ("text/plain".equals(contentType)) {
			mids = importMidFromText.importData(filePart.getInputStream());
		} else {
			log.info("上傳格式錯誤 - contentType = {}", contentType);
			
			throw new BcsNoticeException("上傳格式錯誤");
		}

		log.info("[ Check Exist Mids ] mids.size() = {}", mids.size());
		
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
				log.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} catch (Exception e) {
				log.info("[ Check Exist Mids ] Exception : {}", e);

				endTime = System.currentTimeMillis();
				log.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} finally {

				log.info("[ Check Exist Mids ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
			}

			log.info("[ Check Exist Mids ] existMids.size() : {}", existMids.size());
			
			log.info("---------- Save UserEventSet ----------");
			
			// Start Save BCS_USER_EVENT_SET
			startTime = System.currentTimeMillis();
			log.info("[ Save UserEventSet ] START TIME : {}", sdf.format(new Date(startTime)));
			
			if (existMids != null && existMids.size() > 0) {
				referenceId = UUID.randomUUID().toString().toLowerCase();
				log.info("[ Save UserEventSet ] referenceId : {}", referenceId);

				/*
				 * 增加Try-Catch，判斷Exception是否為Transaction Timeout Exception? 如是，則判斷是否已達Retry上限次數?
				 * 是的話拋出Execption{TimeOut}，否則拋Execption{RetrySaveUserEventSet}。
				 */
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
		                /* 每一千筆處理一次 */
		                if (i % 1000 == 0) {
		                    log.info("userEventSetList size:" + userEventSetList.size());
//		                    log.info("userEventSetList:" + userEventSetList);
		                	userEventSetService.save(userEventSetList);
		                	userEventSetList.clear();
		                }     						
					}
		            /* Update userEventSet */
		            if (!userEventSetList.isEmpty()) {
		                log.info("userEventSetList size:" + userEventSetList.size());
//		                log.info("userEventSetList:" + userEventSetList);
		            	userEventSetService.save(userEventSetList);
		            	userEventSetList.clear();
		            }      					
					endTime = System.currentTimeMillis();
					log.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					log.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));

				} catch (Exception e) {
					endTime = System.currentTimeMillis();
					log.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					log.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));

					if (e.getMessage().contains("transaction timeout expired")) {
						TransactionTimeoutRetry += 1;
						log.info("[ Save UserEventSet ] retry : {}/{}", TransactionTimeoutRetry, TRANSACTION_TIMEOUT_RETRY_MAX_TIMES);

						if (TransactionTimeoutRetry > TRANSACTION_TIMEOUT_RETRY_MAX_TIMES) {
							throw new Exception("TimeOut");
						} else {
							throw new Exception("RetrySaveUserEventSet");
						}
					}
				}

				Map<String, Object> result = new HashMap<String, Object>();

				result.put("referenceId", referenceId);
				result.put("count", existMids.size());
				log.info("result = {}", result);

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
		log.info("uploadRichmenuMidSendGroup");
		
		log.info("---------- Check Exist Mids ----------");
		long startTime = System.currentTimeMillis();
		log.info("[ Check Exist Mids ] START TIME : {}", sdf.format(new Date(startTime)));
		
		long endTime = 0;

		log.info("[ Check Exist Mids ] filePart.getSize() = {}", filePart.getSize());

		fileName = filePart.getOriginalFilename();
		log.info("[ Check Exist Mids ] getOriginalFilename = {}", fileName);

		String contentType = filePart.getContentType();
		log.info("[ Check Exist Mids ] getContentType = {}", contentType);

		this.modifyTime = modifyTime;
		this.modifyUser = modifyUser;

		Set<String> mids = null;
		
		if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) || "application/vnd.ms-excel".equals(contentType)) {
			mids = importMidFromExcel.importData(filePart.getInputStream());
		} else if ("text/plain".equals(contentType)) {
			mids = importMidFromText.importData(filePart.getInputStream());
		} else {
			log.info("上傳格式錯誤 - contentType = {}", contentType);
			
			throw new BcsNoticeException("上傳格式錯誤");
		}

		log.info("[ Check Exist Mids ] mids.size() = {}", mids.size());
		
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
				log.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} catch (Exception e) {
				log.info("[ Check Exist Mids ] Exception : {}", e);

				endTime = System.currentTimeMillis();
				log.info("[ Check Exist Mids ] END TIME : {}", sdf.format(new Date(endTime)));

			} finally {

				log.info("[ Check Exist Mids ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
			}

			log.info("[ Check Exist Mids ] existMids.size() : {}", existMids.size());
			
			log.info("---------- Save UserEventSet ----------");
			
			// Start Save BCS_USER_EVENT_SET
			startTime = System.currentTimeMillis();
			log.info("[ Save UserEventSet ] START TIME : {}", sdf.format(new Date(startTime)));
			
			if (existMids != null && existMids.size() > 0) {
				referenceId = UUID.randomUUID().toString().toLowerCase();
				log.info("[ Save UserEventSet ] referenceId : {}", referenceId);

				/*
				 * 增加Try-Catch，判斷Exception是否為Transaction Timeout Exception? 如是，則判斷是否已達Retry上限次數?
				 * 是的話拋出Execption{TimeOut}，否則拋Execption{RetrySaveUserEventSet}。
				 */
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
//						log.info("[ Save UserEventSet ] userEventSet : {}", userEventSet);
		                /* 效能優化 ： 組裝userEventSetList , 一次儲存 */
		                userEventSetList.add(userEventSet);
		                i++;
		                /* 每一千筆處理一次 */
		                if (i % 1000 == 0) {
		                    log.info("userEventSetList size:" + userEventSetList.size());
//		                    log.info("userEventSetList:" + userEventSetList);
		                	userEventSetService.save(userEventSetList);
		                	userEventSetList.clear();
		                }     	
					}
		            /* Update userEventSet */
		            if (!userEventSetList.isEmpty()) {
		                log.info("userEventSetList size:" + userEventSetList.size());
//		                log.info("userEventSetList:" + userEventSetList);
		            	userEventSetService.save(userEventSetList);
		            	userEventSetList.clear();
		            }      					
		            
					endTime = System.currentTimeMillis();
					log.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					log.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));

				} catch (Exception e) {
					endTime = System.currentTimeMillis();
					log.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));

					log.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));

					if (e.getMessage().contains("transaction timeout expired")) {
						TransactionTimeoutRetry += 1;
						log.info("[ Save UserEventSet ] retry : {}/{}", TransactionTimeoutRetry, TRANSACTION_TIMEOUT_RETRY_MAX_TIMES);

						if (TransactionTimeoutRetry > TRANSACTION_TIMEOUT_RETRY_MAX_TIMES) {
							throw new Exception("TimeOut");
						} else {
							throw new Exception("RetrySaveUserEventSet");
						}
					}
				}

				Map<String, Object> result = new HashMap<String, Object>();

				result.put("referenceId", referenceId);
				result.put("count", existMids.size());
				log.info("result = {}", result);

				existMids.clear();
				return result;
			} else {
				throw new BcsNoticeException("上傳名單無資料或查無對應UID資料");
			}
		} else {
			throw new BcsNoticeException("上傳名單無資料或查無對應UID資料");
		}
	}

	public Map<String, Object> RetrySaveUserEventSet() {
		
		try {
			return RetrySaveUserEventSet(existMids, referenceId, fileName, modifyTime, modifyUser, curSaveIndex);
		} catch (Exception e) {
			log.info("[ RetrySave UserEventSet ] Exception : {}", e);
		}

		return null;
	}

	/* Retry to save UserEventSet */
	@Transactional(rollbackFor = Exception.class, timeout = -1)
	public Map<String, Object> RetrySaveUserEventSet(List<String> existMids, String referenceId, String fileName, Date modifyTime, String modifyUser, int curSaveIndex) throws Exception {

		long retryEndTime = 0;
		long retryStartTime = 0;
		
		this.existMids = existMids;
		this.referenceId = referenceId;
		this.fileName = fileName;
		this.modifyTime = modifyTime;
		this.modifyUser = modifyUser;
		this.curSaveIndex = curSaveIndex;

		log.info("[ RetrySave UserEventSet ] existMids.size() : {}", existMids.size());
		log.info("[ RetrySave UserEventSet ] referenceId : {}", referenceId);
		log.info("[ RetrySave UserEventSet ] fileName : {}", fileName);
		log.info("[ RetrySave UserEventSet ] modifyTime : {}", modifyTime);
		log.info("[ RetrySave UserEventSet ] modifyUser : {}", modifyUser);
		log.info("[ RetrySave UserEventSet ] curSaveIndex : {}", curSaveIndex);
		
		retryStartTime = System.currentTimeMillis();
		log.info("[ RetrySave UserEventSet ] START TIME : {}", retryStartTime);

		try {
            //前面已經優化過, 理論上不應該timeout了.
			for (int i = this.curSaveIndex; i < existMids.size(); i++) {
				String mid = existMids.get(i);

				UserEventSet userEventSet = new UserEventSet();

				userEventSet.setTarget(EVENT_TARGET_ACTION_TYPE.EVENT_SEND_GROUP.toString());
				userEventSet.setAction(EVENT_TARGET_ACTION_TYPE.ACTION_UPLOAD_MID.toString());

				userEventSet.setReferenceId(referenceId);

				userEventSet.setMid(mid);
				userEventSet.setContent(fileName);

				userEventSet.setSetTime(modifyTime);
				userEventSet.setModifyUser(modifyUser);

				log.info("[ RetrySave UserEventSet ] userEventSet : {}", userEventSet);

				userEventSetService.save(userEventSet);
			}

			retryEndTime = System.currentTimeMillis();
			log.info("[ RetrySave UserEventSet ] END TIME : {}", retryEndTime);
			log.info("[ RetrySave UserEventSet ] ELAPSED TIME : {} seconds", ((retryEndTime - retryStartTime) / 1000F));

			Map<String, Object> result = new HashMap<String, Object>();
			result.put("referenceId", referenceId);
			result.put("count", existMids.size());
			log.info("result : {}", result);
			
			return result;

		} catch (Exception e) {
			log.info("[ RetrySave UserEventSet ] Exception : {}", e);
			
			retryEndTime = System.currentTimeMillis();
			log.info("[ RetrySave UserEventSet ] END TIME : {}", retryEndTime);
			log.info("[ RetrySave UserEventSet ] ELAPSED TIME : {} seconds", ((retryEndTime - retryStartTime) / 1000F));

			// 增加retry機制，紀錄當前寫入UserEventSet table 的index， 如果
			// timeout，則重新從index繼續寫入UserEventSet table
			if (e.getMessage().contains("transaction timeout expired")) {
				TransactionTimeoutRetry += 1;
				log.info("[ RetrySave UserEventSet ] retry : {}/{}", TransactionTimeoutRetry, TRANSACTION_TIMEOUT_RETRY_MAX_TIMES);

				if (TransactionTimeoutRetry > TRANSACTION_TIMEOUT_RETRY_MAX_TIMES) {
					throw new Exception("TimeOut");
				} else {
					throw new Exception("RetrySaveUserEventSet");
				}
			}
			
			throw new BcsNoticeException("資料量過大導致超時發生異常，重試失敗。");
		}
	}
}
