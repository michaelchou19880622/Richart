package com.bcs.core.richart.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bcs.core.db.entity.AdminUser;
import com.bcs.core.db.entity.AdminUser.RoleCode;
import com.bcs.core.db.service.AdminUserService;
import com.bcs.core.enums.CONFIG_STR;
import com.bcs.core.resource.CoreConfigReader;
import com.bcs.core.richart.api.model.AdUserSyncModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RichartAdService {
	@Autowired
	private AdminUserService adminUserService;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional(rollbackFor = Exception.class, timeout = 30)
	public void syncAdUser(AdUserSyncModel model) throws Exception {
		log.info("[syncAdUser] AdUserSyncModel = {}", model);

		String account = model.getUserId();
		
		if (StringUtils.isBlank(account)) {
			throw new Exception("AccountNull");
		}

		AdminUser adminUser = adminUserService.findOne(account);
		log.info("[syncAdUser] 1-1 AdminUser = {}", adminUser);

		if (adminUser == null) {
			adminUser = new AdminUser();
			adminUser.setAccount(account);
			adminUser.setModifyTime(new Date());
		}

		log.info("[syncAdUser] 1-2 AdminUser = {}", adminUser);
		
		String prefix = CoreConfigReader.getString(CONFIG_STR.PASSWORD_PREFIX, true);
		String suffix = CoreConfigReader.getString(CONFIG_STR.PASSWORD_SUFFIX, true);
		
		account = account.toLowerCase();
		adminUser.setPassword(passwordEncoder.encode(prefix + account + suffix));

		adminUser.setModifyUser("SYSTEM");

		String name = model.getName();
		
		if (StringUtils.isBlank(name)) {
			throw new Exception("NameNull");
		}
		
		adminUser.setUserName(name);

		String linebc = model.getLinebc();
		String role = "";
		
		boolean roleCheck = false;
		
		if (StringUtils.isBlank(linebc)) {
			throw new Exception("LinebcNull");
		}
		
		RoleCode[] roles = RoleCode.values();
		
		for (RoleCode code : roles) {
			if (code.getRoleName().equals(linebc)) {
				role = code.getRoleId();
				roleCheck = true;
			}
		}
		
		for (RoleCode code : roles) {
			if (code.getRoleNameEn().equals(linebc)) {
				role = code.getRoleId();
				roleCheck = true;
			}
		}
		
		if (roleCheck) {
			adminUser.setRole(role);
		} else {
			throw new Exception("RoleError");
		}

		adminUser.setEmail(model.getEmail());
		adminUser.setTelephone(model.getTelephone());
		adminUser.setDepartment(model.getDepartment());
		adminUser.setPepper(model.getPepper());
		adminUser.setRobot(model.getRobot());
		adminUser.setDesk(model.getDesk());
		adminUser.setLinebc(model.getLinebc());
		adminUser.setRoseline(model.getRoseline());

		adminUser.setViewLimit(model.getViewLimit());
		adminUser.setCanCopy(model.getCanCopy());
		adminUser.setCanSave(model.getCanSave());
		adminUser.setCanPrinting(model.getCanPrinting());

		adminUserService.save(adminUser);
	}
}
