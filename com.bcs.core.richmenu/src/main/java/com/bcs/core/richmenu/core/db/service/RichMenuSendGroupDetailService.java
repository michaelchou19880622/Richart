package com.bcs.core.richmenu.core.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bcs.core.richmenu.core.db.entity.RichMenuSendGroupDetail;
import com.bcs.core.richmenu.core.db.repository.RichMenuSendGroupDetailRepository;

@Service
public class RichMenuSendGroupDetailService {
	@Autowired
	private RichMenuSendGroupDetailRepository sendGroupDetailRepository;

	public void save(RichMenuSendGroupDetail sendGroupDetail){
		sendGroupDetailRepository.save(sendGroupDetail);
	}
}
