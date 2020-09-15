package com.bcs.core.db.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.List;

import com.bcs.core.db.entity.UserUploadList;
import com.bcs.core.db.repository.UserUploadListRepository;

@Service
public class UserUploadListService {
	
	@Autowired
	private UserUploadListRepository userUploadListRepository;
	
	public void save(UserUploadList userEventSet){
		userUploadListRepository.save(userEventSet);
	}
	
	public void save(List<UserUploadList> userEventSetList){
		userUploadListRepository.save(userEventSetList);
	}

	public void flush(){
		userUploadListRepository.flush();
	}
	
	public Page<UserUploadList> findAll(Pageable pageable){
		return userUploadListRepository.findAll(pageable);
	}

	public Page<UserUploadList> findByReferenceId(String referenceId, Pageable pageable){
		return userUploadListRepository.findByReferenceId(referenceId, pageable);
	}
	
	public Page<UserUploadList> findByReferenceIdAndIsExist(String referenceId, int isExist, Pageable pageable){
		return userUploadListRepository.findByReferenceIdAndIsExist(referenceId, isExist, pageable);
	}

	public Long deleteByReferenceId(String referenceId){
		return userUploadListRepository.deleteByReferenceId(referenceId);
	}

	public Long countByReferenceId(String referenceId){
		return userUploadListRepository.countByReferenceId(referenceId);
	}

	public Long countByReferenceIdAndIsExist(String referenceId, int isExist){
		return userUploadListRepository.countByReferenceIdAndIsExist(referenceId, isExist);
	}
	
	public void updateUserIsExist(String referenceId){
		userUploadListRepository.updateUserIsExist(referenceId);
	}
	
}
