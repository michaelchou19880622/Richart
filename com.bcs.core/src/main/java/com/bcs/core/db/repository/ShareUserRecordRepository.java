package com.bcs.core.db.repository;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.LineUser;
import com.bcs.core.db.entity.ShareUserRecord;
import com.bcs.core.db.persistence.EntityRepository;
import com.bcs.core.utils.SQLDateFormatUtil;

public interface ShareUserRecordRepository extends EntityRepository<ShareUserRecord, String>{

    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select * from BCS_SHARE_USER_RECORD where CAMPAIGN_ID = ?1 and UID = ?2", nativeQuery = true)
    ShareUserRecord findByCampaignIdAndUid(String campaignId, String uid);

    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select count(*) as SHARE_NUMBER "
            + "from BCS_SHARE_USER_RECORD SUR "
            + "where CAMPAIGN_ID = ?1", nativeQuery = true)
    Integer countByCampaignId(String campaignId);
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select SUR.UID as SUR_UID, "
            + "SUR.MODIFY_TIME as SUR_MODIFY_TIME, "
            + "SCCT.UID as SCCT_UID, "
            + "SCCT.MODIFY_TIME as SCCT_MODIFY_TIME "
            + "from BCS_SHARE_USER_RECORD SUR "
            + "left join BCS_SHARE_CAMPAIGN_CLICK_TRACING SCCT on SUR.SHARE_USER_RECORD_ID = SCCT.SHARE_USER_RECORD_ID "
            + "where SUR.MODIFY_TIME >= ?1 and SUR.MODIFY_TIME < ?2 and CAMPAIGN_ID = ?3 "
            + "order by SUR.UID, SUR.MODIFY_TIME", nativeQuery = true)
    List<Object[]> findByModifyTimeAndCampaignId(Date start, Date end, String campaignId);
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select "
			    		+ "SUR.UID as SUR_UID, "
			            + "SCCT.SHARED_TIME as SCCT_SHARED_TIME, "
			            + "SCCT.UID as SCCT_UID, "
						+ "IIF ( SLU.CREATE_TIME > SCCT.SHARED_TIME, 1, 0 ) as IS_NEWUSER, "
						+ "IIF ( SLU.BIND_TIME > BSC.START_TIME, 1, 0 ) AS IS_BIND, "
						+ "IIF ( SLU.BIND_TIME is null, 0, 1 ) as BINEISNULL, "
			            + "IIF ( SDR.CAMPAIGN_ID = SUR.CAMPAIGN_ID, 1, IIF ( SLU.MID = SCCT.UID AND SLU.CREATE_TIME > SCCT.SHARED_TIME, 1, 0 ) ) as IS_DONATE, "
			            + "SCCT.MODIFY_TIME as SCCT_MODIFY_TIME, "
			            + "SLU.CREATE_TIME as CREATE_TIME, "
			            + "SLU.BIND_TIME as BINE_TIME, "
			            + "BSC.JUDGEMENT as JUDGEMENT, "
			            + "SUR.SHARE_USER_RECORD_ID as SHARE_USER_RECORD_ID, "
			            + "BSC.SHARE_TIMES as SHARE_TIMES "
		            + "from BCS_SHARE_USER_RECORD SUR "
		            + "left join BCS_SHARE_CAMPAIGN_CLICK_TRACING SCCT on SUR.SHARE_USER_RECORD_ID = SCCT.SHARE_USER_RECORD_ID "
		            + "left join BCS_SHARE_DONATOR_RECORD SDR on SCCT.UID = SDR.DONATOR_UID and SCCT.SHARE_USER_RECORD_ID = SDR.SHARE_USER_RECORD_ID "
					+ "left join BCS_LINE_USER SLU on SCCT.UID = SLU.MID "
					+ "left join BCS_SHARE_CAMPAIGN BSC on BSC.CAMPAIGN_ID = SUR.CAMPAIGN_ID "
		            + "where "
		            	+ "SUR.MODIFY_TIME >= ?1 "
		            	+ "and SUR.MODIFY_TIME < ?2 "
		            	+ "and SUR.CAMPAIGN_ID = ?3 "
		            + "order by "
			            + "SUR.SHARE_USER_RECORD_ID, "
			            + "SCCT.SHARED_TIME", nativeQuery = true)
    List<Object[]> findByModifyTimeAndCampaignId_for_follow_binded(Date start, Date end, String campaignId);
    
    @Transactional(readOnly = true, timeout = 30)
    @Query(value = "select "
			    		+ "SUR.UID as SUR_UID, "
			            + "SCCT.SHARED_TIME as SCCT_SHARED_TIME, "
			            + "SCCT.UID as SCCT_UID, "
						+ "IIF ( SLU.CREATE_TIME > SCCT.SHARED_TIME, 1, 0) as IS_NEWUSER, "
						+ "IIF ( SLU.BIND_TIME > BSC.START_TIME, 1, 0) AS IS_BIND, "
						+ "IIF ( SLU.BIND_TIME is null, 0, 1 ) as BINEISNULL, "
			            + "IIF ( SDR.CAMPAIGN_ID = SUR.CAMPAIGN_ID, 1, 0) as IS_DONATE, "
			            + "SCCT.MODIFY_TIME as SCCT_MODIFY_TIME, "
			            + "SLU.CREATE_TIME as CREATE_TIME, "
			            + "SLU.BIND_TIME as BINE_TIME, "
			            + "BSC.JUDGEMENT as JUDGEMENT, "
			            + "SUR.SHARE_USER_RECORD_ID as SHARE_USER_RECORD_ID, "
			            + "BSC.SHARE_TIMES as SHARE_TIMES "
		            + "from BCS_SHARE_USER_RECORD SUR "
		            + "left join BCS_SHARE_CAMPAIGN_CLICK_TRACING SCCT on SUR.SHARE_USER_RECORD_ID = SCCT.SHARE_USER_RECORD_ID "
		            + "left join BCS_SHARE_DONATOR_RECORD SDR on SCCT.UID = SDR.DONATOR_UID and SCCT.SHARE_USER_RECORD_ID = SDR.SHARE_USER_RECORD_ID "
					+ "left join BCS_LINE_USER SLU on SCCT.UID = SLU.MID "
					+ "left join BCS_SHARE_CAMPAIGN BSC on BSC.CAMPAIGN_ID = SUR.CAMPAIGN_ID "
		            + "where "
	            		+ "SUR.MODIFY_TIME >= ?1 "
	            		+ "and SUR.MODIFY_TIME < ?2 "
	            		+ "and SUR.CAMPAIGN_ID = ?3 "
		            + "order by "
		            	+ "SUR.SHARE_USER_RECORD_ID, "
		            	+ "SCCT.SHARED_TIME", nativeQuery = true)
    List<Object[]> findByModifyTimeAndCampaignId_for_disable(Date start, Date end, String campaignId);
    
    @Transactional(readOnly = true, timeout = 60)
    @Query(value = "select "
    					+ "a.UID, "
			            + "a.MODIFY_TIME, "
			            + "c.ct, "
			            + "( SELECT SUM(src.IS_NEWUSER) FROM " 
			            + "		(" 
			            + "			select " 
			            + "				IIF ( SLU.CREATE_TIME > SCCT.SHARED_TIME, 1, 0 ) as IS_NEWUSER " 
			            + "			from BCS_SHARE_USER_RECORD SUR " 
			            + "			left join BCS_SHARE_CAMPAIGN_CLICK_TRACING SCCT on SUR.SHARE_USER_RECORD_ID = SCCT.SHARE_USER_RECORD_ID " 
			            + "			left join BCS_SHARE_DONATOR_RECORD SDR on SCCT.UID = SDR.DONATOR_UID and SCCT.SHARE_USER_RECORD_ID = SDR.SHARE_USER_RECORD_ID " 
			            + "			left join BCS_LINE_USER SLU on SCCT.UID = SLU.MID " 
			            + "			left join BCS_SHARE_CAMPAIGN BSC on BSC.CAMPAIGN_ID = SUR.CAMPAIGN_ID " 
			            + "			where " 
			            + "				SUR.MODIFY_TIME >= ?1 " 
			            + "				and SUR.MODIFY_TIME < ?2 " 
			            + "				and SUR.CAMPAIGN_ID = ?3 " 
			            + "		) src " 
			            + ") as sumNewer "
			            + "from BCS_SHARE_USER_RECORD a "
			            + "join BCS_SHARE_CAMPAIGN b on a.CAMPAIGN_ID = b.CAMPAIGN_ID "
			            + "join (select SHARE_USER_RECORD_ID, count(*) as ct from BCS_SHARE_CAMPAIGN_CLICK_TRACING group by SHARE_USER_RECORD_ID) c on c.SHARE_USER_RECORD_ID = a.SHARE_USER_RECORD_ID "
		            + "where "
			            + "a.MODIFY_TIME >= ?1 "
			            + "and a.MODIFY_TIME < ?2 "
			            + "and a.CAMPAIGN_ID = ?3 "
			            + "and a.CUMULATIVE_COUNT >= b.SHARE_TIMES "
		            + "order by "
		            	+ "a.MODIFY_TIME ", nativeQuery = true)
    List<Object[]> findCompletedByModifyTimeAndCampaignId_for_follow_binded(Date start, Date end, String campaignId);

    @Transactional(readOnly = true, timeout = 60)
    @Query(value = "select a.UID, "
			            + "a.DONE_TIME, "
			            + "c.ct, "
			            + "(select count(*) from BCS_SHARE_CAMPAIGN_CLICK_TRACING where SHARE_USER_RECORD_ID = c.SHARE_USER_RECORD_ID AND (UID IS NOT NULL AND SHARED_TIME IS NOT NULL )) as archivedCount "
		            + "from BCS_SHARE_USER_RECORD a "
		            + "join BCS_SHARE_CAMPAIGN b on a.CAMPAIGN_ID = b.CAMPAIGN_ID "
		            + "join (select SHARE_USER_RECORD_ID, count(*) as ct from BCS_SHARE_CAMPAIGN_CLICK_TRACING group by SHARE_USER_RECORD_ID) c on c.SHARE_USER_RECORD_ID = a.SHARE_USER_RECORD_ID "
		            + "where "
			            + "a.MODIFY_TIME >= ?1 "
			            + "and a.MODIFY_TIME < ?2 "
			            + "and a.CAMPAIGN_ID = ?3 "
			            + "and a.CUMULATIVE_COUNT >= b.SHARE_TIMES "
		            + "order by "
		            	+ "a.MODIFY_TIME", nativeQuery = true)
    List<Object[]> findCompletedByModifyTimeAndCampaignId_for_disable(Date start, Date end, String campaignId);
    
    @Transactional(readOnly = true, timeout = 60)
    @Query(value = "select a.UID, "
			            + "a.MODIFY_TIME, "
			            + "case when c.ct is null then 0 else c.ct end as ShareTimes, "
			            + "a.CUMULATIVE_COUNT "
		            + "from BCS_SHARE_USER_RECORD a "
		            + "join BCS_SHARE_CAMPAIGN b on a.CAMPAIGN_ID = b.CAMPAIGN_ID "
		            + "left join (select SHARE_USER_RECORD_ID, count(*) as ct from BCS_SHARE_CAMPAIGN_CLICK_TRACING group by SHARE_USER_RECORD_ID) c on c.SHARE_USER_RECORD_ID = a.SHARE_USER_RECORD_ID "
		            + "where "
			            + "a.MODIFY_TIME >= ?1 "
			            + "and a.MODIFY_TIME < ?2 "
			            + "and a.CAMPAIGN_ID = ?3 "
			            + "and (a.CUMULATIVE_COUNT < b.SHARE_TIMES or c.ct is null) "
		            + "order by "
		            	+ "a.MODIFY_TIME", nativeQuery = true)
    List<Object[]> findUncompletedByModifyTimeAndCampaignId(Date start, Date end, String campaignId);
    
    @Transactional(readOnly = true, timeout = 60)
    @Query(value = "select * from BCS_SHARE_USER_RECORD " +
    		"where COMPLETE_STATUS = 'UNDONE' " + 
    		"and MODIFY_TIME >= DATEADD(day, -1, GETDATE()) and MODIFY_TIME < GETDATE() ", nativeQuery = true)
    List<ShareUserRecord> findLatelyUndoneUsers();
}
