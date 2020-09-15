package com.bcs.web.ui.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bcs.core.bot.db.entity.MsgBotReceive;
import com.bcs.core.db.entity.LineUser;
import com.bcs.core.db.entity.SendGroup;
import com.bcs.core.db.entity.SendGroupDetail;
import com.bcs.core.db.entity.UserEventSet;
import com.bcs.core.db.entity.UserUploadList;
import com.bcs.core.db.repository.SendGroupDetailRepository;
import com.bcs.core.db.service.LineUserService;
import com.bcs.core.db.service.SendGroupService;
import com.bcs.core.db.service.UserEventSetService;
import com.bcs.core.db.service.UserUploadListService;
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
	@Autowired
	private UserUploadListService userUploadListService;

	private static int TRANSACTION_TIMEOUT_RETRY_MAX_TIMES = 3;

	private final static int DATATYPE_DEFAULT = 0;
	private final static int DATATYPE_RICHMENU = 1;
	
	private List<String> existMids = new ArrayList<String>();
	private String uploadReferenceId;
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
	@Transactional(rollbackFor = Exception.class, timeout = 1150)
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
		try {
			if (list != null) {
				for (SendGroupDetail detail : list) {
					detail.setSendGroup(sendGroup);
					sendGroup.getSendGroupDetail().add(detail);
					String referenceId = detail.getQueryValue().split(":")[0];;
					saveMidSendGroup(referenceId);			
				}
			}
		}
		catch (Exception e) {
			throw new BcsNoticeException("TimeOut");
		}				
		// Save SendGroup Again With SendGroupDetail
		sendGroupService.save(sendGroup);
		
		sendGroup = sendGroupService.findOne(sendGroup.getGroupId());
		
		createSystemLog(action, sendGroup, sendGroup.getModifyUser(), sendGroup.getModifyTime(), sendGroup.getGroupId().toString());

		logger.info("sendGroup = {}", sendGroup);
		
		return sendGroup;
	}

	/**
	 * 刪除發送群組
	 * 
	 * @param groupId
	 * @param adminUserAccount
	 * @throws BcsNoticeException
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 300)
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
	@Transactional(rollbackFor = Exception.class, timeout = 1150)
	public Map<String, Object> uploadMidSendGroupByType(MultipartFile filePart, String modifyUser, Date modifyTime, int dataType) throws Exception {
		logger.info("uploadMidSendGroupByType");
		
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
		
		InputStream inputStream = filePart.getInputStream();
		
		Long existMidsCount = 0L;
		
		if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equals(contentType) || "application/vnd.ms-excel".equals(contentType) || "text/csv".equals(contentType)) {
			mids = importMidFromExcel.importData(inputStream);
		} else if ("text/plain".equals(contentType)) {
			mids = importMidFromText.importData(inputStream);
		} else {
			logger.info("上傳格式錯誤 - contentType = {}", contentType);
			
			throw new BcsNoticeException("上傳格式錯誤");
		}

		logger.info("[ Check Exist Mids ] mids.size() = {}", mids.size());
		
		// Check Exist Mids
		if (mids != null && mids.size() > 0) {
			list = new ArrayList<String>(mids);

			try {
				logger.info("---------- Upload UserUploadList Begin ----------");
				
				startTime = System.currentTimeMillis();
				logger.info("[ Upload UserUploadList ] START TIME : {}", sdf.format(new Date(startTime)));
				
				uploadReferenceId = UUID.randomUUID().toString().toLowerCase();
				logger.info("[ Upload UserUploadList ] uploadReferenceId : {}", uploadReferenceId);
				
					try {
						curSaveIndex = 0;

			            int i = 0;
			            List<UserUploadList> userUploadListArrayList = new ArrayList<>();
			            for (String mid : mids) {
	            			
			            	String tmpMid = mid;
			            	
			            	/* 檢查 mid 是否符合Line User ID Regular Expression 且開頭為小寫u? 如果符合，則將開頭的u轉為大寫U */
			            	if (mid.matches("^u[0-9a-f]{32}$")) {
		            			tmpMid = mid.substring(0, 1).toUpperCase() + mid.substring(1);
			            	}
			            	
							UserUploadList userUploadList = new UserUploadList();
							if (dataType == DATATYPE_RICHMENU) {
								userUploadList.setTarget(EVENT_TARGET_ACTION_TYPE.TARGET_RICHMENU_SEND_GROUP.toString());
								userUploadList.setAction(EVENT_TARGET_ACTION_TYPE.ACTION_UPLOAD_RICHMENU_MID.toString());
							}
							else {
								userUploadList.setTarget(EVENT_TARGET_ACTION_TYPE.EVENT_SEND_GROUP.toString());
								userUploadList.setAction(EVENT_TARGET_ACTION_TYPE.ACTION_UPLOAD_MID.toString());
							}							
							
							userUploadList.setReferenceId(uploadReferenceId);
							userUploadList.setMid(tmpMid);
							userUploadList.setContent(fileName);
							userUploadList.setSetTime(modifyTime);
							userUploadList.setModifyUser(modifyUser);
							
			                /* 效能優化 ： 組裝userUploadListArrayList , 一次儲存 */
							userUploadListArrayList.add(userUploadList);
			                i++;
			                /* 每一萬筆處理一次 */
			                if (i % 100000 == 0) {
			                    logger.info("userUploadListArrayList size:" + userUploadListArrayList.size());
			                	userUploadListService.save(userUploadListArrayList);
			                	userUploadListService.flush();
			                	userUploadListArrayList.clear();
			                }     						
						}
			            /* Update userUploadList */
			            if (!userUploadListArrayList.isEmpty()) {
		                    logger.info("userUploadListArrayList size:" + userUploadListArrayList.size());
		                	userUploadListService.save(userUploadListArrayList);
		                	userUploadListService.flush();		                	
		                	userUploadListArrayList.clear();
			            }      					

						endTime = System.currentTimeMillis();
						logger.info("[ Save UserUploadList ] END TIME : {}", sdf.format(new Date(endTime)));
						logger.info("[ Save UserUploadList ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
						
						startTime = System.currentTimeMillis();
						//實際flush到db, 需要花費比較長的時間.
						userUploadListService.updateUserIsExist(uploadReferenceId);						
						endTime = System.currentTimeMillis();

						logger.info("[ Update UserUploadListService ] END TIME : {}", sdf.format(new Date(endTime)));
						logger.info("[ Update UserUploadListService ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
												
						startTime = System.currentTimeMillis();
						int isExist = 1;
						existMidsCount = userUploadListService.countByReferenceIdAndIsExist(uploadReferenceId, isExist);
						endTime = System.currentTimeMillis();

						logger.info("[ Count UserUploadListService Exist Mids] END TIME : {}", sdf.format(new Date(endTime)));
						logger.info("[ Count UserUploadListService Exist Mids] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
						
						
					} catch (Exception e) {
						endTime = System.currentTimeMillis();
						logger.info("[ Upload UserUploadList ] END TIME : {}", sdf.format(new Date(endTime)));
						logger.info("[ Upload UserUploadList ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
			            log.error("Exception", e);
						throw new Exception("TimeOut");									
					}			
				endTime = System.currentTimeMillis();
				logger.info("[ Upload UserUploadList ] END TIME : {}", sdf.format(new Date(endTime)));

			} catch (Exception e) {
				logger.info("[ Upload UserUploadList ] Exception : {}", e);
				endTime = System.currentTimeMillis();
				logger.info("[ Upload UserUploadList ] END TIME : {}", sdf.format(new Date(endTime)));

			} finally {
				logger.info("[ Upload UserUploadList ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
			}
			try {				
				Map<String, Object> result = new HashMap<String, Object>();	
				result.put("referenceId", uploadReferenceId);
				result.put("count", existMidsCount);
				logger.info("result = {}", result);

				mids.clear();
				inputStream.close();
				
				logger.info("---------- Upload UserUploadList End ----------");
				return result;
			}
			catch (Exception e) {
				endTime = System.currentTimeMillis();
				logger.info("[ Upload UserUploadList ] END TIME : {}", sdf.format(new Date(endTime)));
				logger.info("[ Upload UserUploadList ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
				throw new Exception("TimeOut");			
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
	@Transactional(rollbackFor = Exception.class, timeout = 1150)
	public void saveMidSendGroup(String uploadReferenceId) throws Exception {
		logger.info("---------- SaveMidSendGroup Begin ---------- referenceID : {}", uploadReferenceId);
		int page = 0;
		int pageSize = 100000;
		int isExist = 1;
		long startTime = 0L;
		long endTime = 0L;
		int i = 0;
		
		Sort.Order order = new Sort.Order(Direction.DESC, "referenceId");
		Sort sort = new Sort(order);	
		try {
		
			while(true) {
				startTime = System.currentTimeMillis();
				Pageable pageable = new PageRequest(page, pageSize, sort);
				Page<UserUploadList> result = userUploadListService.findByReferenceIdAndIsExist(uploadReferenceId, isExist, pageable);
				if(result != null){
					List<UserUploadList> userUploadLists =  result.getContent();
					if(userUploadLists != null && userUploadLists.size() > 0){								
						i = 0;
						List<UserEventSet> userEventSetList = new ArrayList<>();
						for (UserUploadList userUpload : userUploadLists) {								            			
							UserEventSet userEventSet = new UserEventSet();
							userEventSet.setTarget(userUpload.getTarget());
							userEventSet.setAction(userUpload.getAction());
							userEventSet.setReferenceId(userUpload.getReferenceId());
							userEventSet.setMid(userUpload.getMid());
							userEventSet.setContent(userUpload.getContent());
							userEventSet.setSetTime(userUpload.getSetTime());
							userEventSet.setModifyUser(userUpload.getModifyUser());
							/* 效能優化 ： 組裝userEventSetList , 一次儲存 */
							userEventSetList.add(userEventSet);
							i++;
							/* 每十萬筆處理一次 */
							if (i % pageSize == 0) {
								logger.info("userEventSetList size:" + userEventSetList.size());
								userEventSetService.save(userEventSetList);
								userEventSetService.flush();
								userEventSetList.clear();
							}     						
						}
						/* Update userEventSet */
						if (!userEventSetList.isEmpty()) {
							logger.info("userEventSetList size:" + userEventSetList.size());
							userEventSetService.save(userEventSetList);
							userEventSetService.flush();						
							userEventSetList.clear();
							break;
						}		
					}
					else{
						break;
					}					
				}
				else{
					break;
				}							
				page++;							
				endTime = System.currentTimeMillis();
				logger.info("[ Save UserEventSet ] END TIME : {}, page : {}", sdf.format(new Date(endTime)), page);
				logger.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds, page : {}", ((endTime - startTime) / 1000F), page);							
			}
			logger.info("---------- SaveMidSendGroup End ---------- referenceID : {}", uploadReferenceId);
			
			startTime = System.currentTimeMillis();
			userUploadListService.deleteByReferenceId(uploadReferenceId);
			userUploadListService.flush();
			endTime = System.currentTimeMillis();
			logger.info("[ Delete UserUploadList ] END TIME : {}, referenceId : {}", sdf.format(new Date(endTime)), uploadReferenceId);
			logger.info("[ Delete UserUploadList ] ELAPSED TIME : {} seconds, referenceId : {}", ((endTime - startTime) / 1000F), uploadReferenceId);							
			
		}
		catch (Exception e) {
			endTime = System.currentTimeMillis();
			logger.info("[ Save UserEventSet ] END TIME : {}", sdf.format(new Date(endTime)));
			logger.info("[ Save UserEventSet ] ELAPSED TIME : {} seconds", ((endTime - startTime) / 1000F));
			throw new Exception("TimeOut");			
		}
	}
	
	/**
	 * @param filePart
	 * @param modifyUser
	 * @param modifyTime
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 1150)
	public Map<String, Object> uploadMidSendGroup(MultipartFile filePart, String modifyUser, Date modifyTime) throws Exception {
		logger.info("uploadMidSendGroup");		
		Map<String, Object> result = new HashMap<String, Object>();	
		try {
			result = uploadMidSendGroupByType(filePart, modifyUser, modifyTime, DATATYPE_DEFAULT);
		}
		catch (Exception e) {
			throw e;			
		}
		return result;	
	}
	/**
	 * @param filePart
	 * @param modifyUser
	 * @param modifyTime
	 * @return
	 * @throws Exception
	 */
	@Transactional(rollbackFor = Exception.class, timeout = 1150)
	public Map<String, Object> uploadRichmenuMidSendGroup(MultipartFile filePart, String modifyUser, Date modifyTime) throws Exception {
		logger.info("uploadRichmenuMidSendGroup");		
		Map<String, Object> result = new HashMap<String, Object>();	
		try {
			result = uploadMidSendGroupByType(filePart, modifyUser, modifyTime, DATATYPE_RICHMENU);
		}
		catch (Exception e) {
			throw e;			
		}
		return result;
	}
}
