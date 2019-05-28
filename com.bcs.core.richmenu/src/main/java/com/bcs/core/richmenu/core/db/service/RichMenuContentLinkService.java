package com.bcs.core.richmenu.core.db.service;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.bcs.core.richmenu.core.db.entity.RichMenuContentLink;
import com.bcs.core.richmenu.core.db.repository.RichMenuContentLinkRepository;

@Service
public class RichMenuContentLinkService {
	
    @PersistenceContext
    EntityManager entityManager;
    
	/** Logger */
	private static Logger logger = Logger.getLogger(RichMenuContentLinkService.class);
	
	@Autowired
	private RichMenuContentLinkRepository contentLinkRepository;

	protected LoadingCache<String, RichMenuContentLink> dataCache; // No Need Sync

	public RichMenuContentLinkService(){

		dataCache = CacheBuilder.newBuilder()
				.concurrencyLevel(1)
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.build(new CacheLoader<String, RichMenuContentLink>() {
					@Override
					public RichMenuContentLink load(String key) throws Exception {
						return new RichMenuContentLink("-");
					}
				});
	}
	
	@PreDestroy
	public void cleanUp() {
		logger.info("[DESTROY] ContentLinkService cleaning up...");
		try{
			if(dataCache != null){
				dataCache.invalidateAll();
				dataCache = null;
			}
		}
		catch(Throwable e){}
		
		System.gc();
		logger.info("[DESTROY] ContentLinkService destroyed.");
	}
	
	private boolean notNull(RichMenuContentLink result){
		if(result != null && StringUtils.isNotBlank(result.getLinkId()) && !"-".equals(result.getLinkId())){
			return true;
		}
		return false;
	}
    
    /**
  	 * 取得所有連結清單
       */
  	public List<Object[]> getAllContentLinkUrl(){
		return contentLinkRepository.findAllLinkUrl();
    }
  	
  	public List<Object[]> findAllLinkUrlByFlag(String flag){
  		return contentLinkRepository.findAllLinkUrlByFlag(flag);
  	}
  	
  	public List<Object[]> findAllLinkUrlByLikeFlag(String flag){
  		return contentLinkRepository.findAllLinkUrlByLikeFlag(flag);
  	}
  	
  	public List<Object[]> findAllLinkUrlByLikeTitle(String title){
  		return contentLinkRepository.findAllLinkUrlByLikeTitle(title);
  	}

  	public List<RichMenuContentLink> findAll(){
		return contentLinkRepository.findAll();
    }

  	public Page<RichMenuContentLink> findAll(Pageable pageable){
		return contentLinkRepository.findAll(pageable);
    }

  	public List<RichMenuContentLink> findByLinkUrl(String linkUrl){
		return contentLinkRepository.findByLinkUrl(linkUrl);
  	}
  	
  	public List<RichMenuContentLink> findByLinkIdIn(List<String> linkIds){
  		return contentLinkRepository.findByLinkIdIn(linkIds);
  	}
	
	public void save(RichMenuContentLink contentLink){
		contentLinkRepository.save(contentLink);

		if(contentLink != null){
			dataCache.put(contentLink.getLinkId(), contentLink);
		}
	}
	
	public void save(List<RichMenuContentLink> contentLinks){
		for(RichMenuContentLink contentLink : contentLinks){
			this.save(contentLink);
		}
	}
	
	public RichMenuContentLink findOne(String linkId){
		try {
			RichMenuContentLink result = dataCache.get(linkId);
			if(notNull(result)){
				return result;
			}
		} catch (Exception e) {}
		
		RichMenuContentLink result = contentLinkRepository.findOne(linkId);
		if(result != null){
			dataCache.put(linkId, result);
		}
		return result;
	}
	
	public List<Object[]> countClickCountByLinkUrlAndTime(String linkUrl, String start, String end){
		return contentLinkRepository.countClickCountByLinkUrlAndTime(linkUrl, start, end);
	}
	
	public List<Object[]> countClickCountByLinkUrlAndTime(String linkUrl, String day){
		return contentLinkRepository.countClickCountByLinkUrlAndTime(linkUrl, day);
	}
	
	public List<Object[]> countClickCountByLinkUrl(String linkUrl){
		return contentLinkRepository.countClickCountByLinkUrl(linkUrl);
	}
	
	public List<Object[]> countClickCountByLinkUrl(String linkUrl, String start){
		return contentLinkRepository.countClickCountByLinkUrl(linkUrl, start);
	}
	
	public List<Object[]> countClickCountByLinkIdAndTime(String linkUrl, String start, String end){
		return contentLinkRepository.countClickCountByLinkIdAndTime(linkUrl, start, end);
	}
	
	public List<Object[]> countClickCountByLinkId(String LinkId){
		return contentLinkRepository.countClickCountByLinkId(LinkId);
	}
	
	public List<Object[]> countClickCountByLinkId(String LinkId, String start){
		return contentLinkRepository.countClickCountByLinkId(LinkId, start);
	}
	
	public List<String> findClickMidByLinkUrlAndTime(String linkUrl, String start, String end){
		return contentLinkRepository.findClickMidByLinkUrlAndTime(linkUrl, start, end);
	}
	
	public Long countUrl() {
	    return contentLinkRepository.countUrl();
	}
	
	public Long countUrlByLikeFlagOrTitle(String flag) {
	    return contentLinkRepository.countUrlByLikeFlagOrTitle(flag);
	}
    
    public List<RichMenuContentLink> findByLikeFlagOrTitle(String flag, int page, int size, boolean isAsc){
        
        Sort.Order order = new Sort.Order(isAsc? Direction.ASC : Direction.DESC, "modifyTime");
        Sort sort = new Sort(order);
        Pageable pageable = new PageRequest(page, size, sort);

        if(StringUtils.isBlank(flag)) {
            return contentLinkRepository.findAllLink(pageable).getContent();
            
        }else {
            return contentLinkRepository.findByLikeFlagOrTitle("%" + flag + "%", pageable).getContent();
        }
    }
    
    public List<RichMenuContentLink> findContentLinkByRichId(String richId){
    	return contentLinkRepository.findContentLinkByRichId(richId);        
    }
}
