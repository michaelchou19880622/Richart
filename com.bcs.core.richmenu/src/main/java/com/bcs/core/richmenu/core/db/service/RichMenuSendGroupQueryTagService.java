package com.bcs.core.richmenu.core.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.richmenu.core.db.entity.RichMenuSendGroupQueryTag;
import com.bcs.core.richmenu.core.db.repository.RichMenuSendGroupQueryTagRepository;

@Service
public class RichMenuSendGroupQueryTagService {
	@Autowired
	private RichMenuSendGroupQueryTagRepository sendGroupQueryTagRepository;

	public long countBySendGroupQueryQueryFieldIdAndQueryFieldTagValue(String queryFieldId, String queryFieldTagValue) {
		return sendGroupQueryTagRepository.countBySendGroupQueryQueryFieldIdAndQueryFieldTagValue(queryFieldId, queryFieldTagValue);
	}
	
	public void save(RichMenuSendGroupQueryTag sendGroupQueryTag){
		sendGroupQueryTagRepository.save(sendGroupQueryTag);
	}
}
