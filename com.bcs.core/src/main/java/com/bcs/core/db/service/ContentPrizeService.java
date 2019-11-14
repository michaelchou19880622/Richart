package com.bcs.core.db.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.ContentCoupon;
import com.bcs.core.db.entity.ContentPrize;
import com.bcs.core.db.entity.PrizeList;
import com.bcs.core.db.repository.ContentCouponRepository;
import com.bcs.core.db.repository.ContentPrizeRepository;
import com.bcs.core.db.repository.PrizeListRepository;

@Service
public class ContentPrizeService {
	@Autowired
	private ContentPrizeRepository contentPrizeRepository;
	@Autowired
	private PrizeListRepository prizeListRepository;
	@Autowired
	private ContentCouponRepository contentCouponRepository;
	@Autowired
	private ActionUserCouponService actionUserCouponService;
	
	/** Logger */
	private static Logger logger = Logger.getLogger(ContentPrizeService.class);

	/**
	 * 隨機抽選一個獎品
	 * @throws Exception 
	 */
	public String getRandomPrize(String gameId, String mid) throws Exception {
		logger.info("getRandomPrize, gameId=" + gameId + " mid=" + mid);
		ContentCoupon drewCoupon = null;
		List<ContentCoupon> contentCouponList = contentCouponRepository.findByEventReferenceAndEventReferenceIdAndCouponRemain(ContentCoupon.EVENT_REFERENCE_SCRATCH_CARD, gameId);
		String drewCouponId = null;
		Random random = new Random();
		BigDecimal index = new BigDecimal(random.ints(1, 10000).findFirst().getAsInt());
		BigDecimal accumulation = new BigDecimal("0");
		List<ContentCoupon> UnLimitContentCouponIdList = new ArrayList<ContentCoupon>() ;
		int prizeCount = contentCouponList.size();
		
		logger.info("contentCouponList : " + contentCouponList);
		logger.info("index : " + index );
		for (int i = 0; i < prizeCount; i++) {
			accumulation = accumulation.add(contentCouponList.get(i).getProbability());
			logger.info("目前累進的機率 : " + accumulation );
			//取得所有無上限的優惠卷
			if(null == contentCouponList.get(i).getCouponGetLimitNumber()) {
				UnLimitContentCouponIdList.add(contentCouponList.get(i));
			}
			
			/* 判斷此優惠券是否為中獎優惠券 */
			if (accumulation.multiply(new BigDecimal("100")).compareTo(index) == 1) {
				drewCoupon = contentCouponList.get(i);

				logger.info("◎ 抽中的優惠券：" + drewCoupon);

				/* 判斷此優惠券無數量限制或是還有剩餘的數量， */
				if ((drewCoupon.getCouponGetLimitNumber() == null || drewCoupon.getCouponGetLimitNumber() == 0)
						|| (drewCoupon.getCouponGetLimitNumber() - drewCoupon.getCouponGetNumber()) > 0) {
					Date today = new Date();
					
					/* 判斷此優惠券是否在可領取的期間，如果是的話，便將 drewCouponId 設為此優惠券 id */
					if(today.compareTo(drewCoupon.getCouponStartUsingTime()) >= 0 && today.compareTo(drewCoupon.getCouponEndUsingTime()) < 0) {
						drewCouponId = drewCoupon.getCouponId();
						Date startUsingDate = drewCoupon.getCouponStartUsingTime();
						Date endUsingDate = drewCoupon.getCouponEndUsingTime();
						actionUserCouponService.createActionUserCoupon(mid, drewCouponId, startUsingDate, endUsingDate);
						break;
					}
				}
			}
		}
		
		if(null == drewCouponId &&  UnLimitContentCouponIdList.size() != 0) {
			logger.info("drewCouponId is null and  UnLimitContentCouponIdList.size() != 0 ");
			logger.info("UnLimitContentCouponIdList : " + UnLimitContentCouponIdList );
			
			if(UnLimitContentCouponIdList.size() == 1) {
				drewCoupon = UnLimitContentCouponIdList.get(0);
			}else {
				int randomUnLimit = random.nextInt(UnLimitContentCouponIdList.size());
				drewCoupon = UnLimitContentCouponIdList.get(randomUnLimit);
			}
			logger.info("◎ 抽中的優惠券   CouponId  ：" +  UnLimitContentCouponIdList.get(0).getCouponId());
			logger.info("◎ 抽中的優惠券  ：" + drewCoupon);
			Date today = new Date();
			/* 判斷此優惠券是否在可領取的期間，如果是的話，便將 drewCouponId 設為此優惠券 id */
			if(today.compareTo(drewCoupon.getCouponStartUsingTime()) >= 0 && today.compareTo(drewCoupon.getCouponEndUsingTime()) < 0) {
				drewCouponId = drewCoupon.getCouponId();
				Date startUsingDate = drewCoupon.getCouponStartUsingTime();
				Date endUsingDate = drewCoupon.getCouponEndUsingTime();
				actionUserCouponService.createActionUserCoupon(mid, drewCouponId, startUsingDate, endUsingDate);
			}
		}
		return drewCouponId;
	}
	
	public ContentCoupon getRandomPrizeNew(String gameId, String mid) throws Exception {
		logger.info("getRandomPrizeNew, gameId=" + gameId + " mid=" + mid);
		ContentCoupon drewCoupon = null, unDrewLimitCoupon = null;
		Random random = new Random();
		BigDecimal index = new BigDecimal(random.ints(1, 10000).findFirst().getAsInt());
		BigDecimal accumulation = new BigDecimal("0");
		Date today = new Date();
		List<ContentCoupon> contentCouponList = contentCouponRepository.findByEventReferenceAndEventReferenceIdAndCouponRemain(ContentCoupon.EVENT_REFERENCE_SCRATCH_CARD, gameId);
		int prizeCount = 0;
		if (contentCouponList != null) {
			prizeCount = contentCouponList.size();
		}
		logger.info("getRandomPrizeNew, index=" + index + " contentCouponList=" + contentCouponList);
		for (int i = 0; i < prizeCount; i++) {
		    ContentCoupon coupon = contentCouponList.get(i);
    		// 取得無上限且在領取期間且領取數目最少的優惠卷
	    	if (coupon.getCouponGetLimitNumber() == null &&
		   		(today.compareTo(coupon.getCouponStartUsingTime()) >= 0 && today.compareTo(coupon.getCouponEndUsingTime()) < 0)) {
	    		if (unDrewLimitCoupon == null || coupon.getCouponGetNumber() < unDrewLimitCoupon.getCouponGetNumber()) {
	    			unDrewLimitCoupon = coupon;
	    		}
		    }
    		accumulation = accumulation.add(coupon.getProbability());
	    	logger.info("getRandomPrizeNew, accumulation =" + accumulation);
		   	// 判斷此優惠券是否為中獎優惠券
		    if (accumulation.multiply(new BigDecimal("100")).compareTo(index) == 1) {
			    logger.info("getRandomPrizeNew, randomCoupon=" + coupon);
		   		// 判斷此優惠券無數量限制或是還有剩餘的數量
		    	if (coupon.getCouponGetLimitNumber() == null || 
			    	(coupon.getCouponGetLimitNumber() - coupon.getCouponGetNumber()) > 0) {
		    		// 判斷此優惠券是否在可領取的期間，如果是的話此優惠券被抽取
			    	if (today.compareTo(coupon.getCouponStartUsingTime()) >= 0 && today.compareTo(coupon.getCouponEndUsingTime()) < 0) {
				    	drewCoupon = coupon;
				    	logger.info("getRandomPrizeNew, drewCouponId=" + drewCoupon.getCouponId());
					    actionUserCouponService.createActionUserCoupon(mid, drewCoupon.getCouponId(), drewCoupon.getCouponStartUsingTime(), drewCoupon.getCouponEndUsingTime());
					    break;
				    }
			    }
		   	}
		}
		if (drewCoupon == null && unDrewLimitCoupon != null) {
			drewCoupon = unDrewLimitCoupon;
			logger.info("getRandomPrizeNew, drewCouponId=" + drewCoupon.getCouponId());
			actionUserCouponService.createActionUserCoupon(mid, drewCoupon.getCouponId(), drewCoupon.getCouponStartUsingTime(), drewCoupon.getCouponEndUsingTime());
		}
		return drewCoupon;
	}

	/**
	 * 
	 */
	public ContentPrize getPrizeByPrizeListId(Integer prizeListId) {
		String prizeId = "";
		ContentPrize contentPrize = new ContentPrize();

		prizeId = prizeListRepository.findOne(prizeListId).getPrizeId();
		logger.info("@@@@@@@@@@@prizeId" + prizeId);
		contentPrize = contentPrizeRepository.findOne(prizeId);

		return contentPrize;
	}

	/**
	 * 
	 */
	public void retrievePrize(Integer prizeListId) {
		PrizeList prize = prizeListRepository.findOne(prizeListId);
		prize.setStatus(PrizeList.PRIZE_STATUS_NOT_WINNED);
		prizeListRepository.save(prize);
	}

	public List<PrizeList> findNotAcceptedByMid(String mid) {
		return prizeListRepository.findNotAcceptedByMid(mid);
	}
}
