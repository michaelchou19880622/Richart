package com.bcs.core.richart.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.db.entity.Tag;
import com.bcs.core.db.repository.TagRepository;

@Service
public class TagService {

	/** Logger **/
	private static Logger logger = LogManager.getLogger(TagService.class);

	@Autowired
	private TagRepository tagRepository;
	
	public Long save(Tag tagSrc) {
		long startTime = System.currentTimeMillis();
		
		Tag tag = tagRepository.save(tagSrc);

		long endTime = System.currentTimeMillis();
		logger.info("[ save ] Elapsed Time : {}", (endTime - startTime));

		logger.info("[ save ] tag = {}", tag);
		
		return tag.getId();
	}
	
	public Tag findById(Long id) {
		long startTime = System.currentTimeMillis();
		
		Tag tag = tagRepository.findById(id);
	
		long endTime = System.currentTimeMillis();
		logger.info("[ findById ] Elapsed Time : {}", (endTime - startTime));
	
		logger.info("[ findById ] tag = {}", tag);
		
		return tag;
	}
	
	public Tag findByTagName(String name) {
		long startTime = System.currentTimeMillis();
		
		Tag tag = tagRepository.findByTagName(name);
	
		long endTime = System.currentTimeMillis();
		logger.info("[ findByName ] Elapsed Time : {}", (endTime - startTime));
	
		logger.info("[ findByName ] tag = {}", tag);
		
		return tag;
	}
}
