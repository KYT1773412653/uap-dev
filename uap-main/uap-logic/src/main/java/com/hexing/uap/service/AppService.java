package com.hexing.uap.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import com.hexing.uap.service.cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.hexing.uap.bean.jpa.UapApp;
import com.hexing.uap.bean.jpa.UapCode;
import com.hexing.uap.bean.jpa.UapMenu;
import com.hexing.uap.bean.jpa.UapRestApi;
import com.hexing.uap.bean.jpa.UapRole;
import com.hexing.uap.bean.jpa.UapTenancyApp;
import com.hexing.uap.common.UapConstant;
import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.common.session.SessionUser;
import com.hexing.uap.repository.AppRepository;
import com.hexing.uap.repository.CodeRepository;
import com.hexing.uap.repository.MenuRepository;
import com.hexing.uap.repository.RestApiRepository;
import com.hexing.uap.repository.RoleRepository;
import com.hexing.uap.repository.TenancyAppRepository;
import com.hexing.uap.repository.custom.AppCustomRepository;
import com.hexing.uap.repository.param.AppQuery;
import com.hexing.uap.service.cache.TenancyCache;
import org.springframework.util.StringUtils;

/**
 * @author Chent
 *
 */
@Service
public class AppService {
	@Autowired
	AppCustomRepository appCustomRepository;
	@Autowired
	AppRepository appRepository;
	@Autowired
	CodeRepository codeRepository;
	@Autowired
	MenuRepository menuRepository;
	@Autowired
	RestApiRepository restApiRepository;
	@Autowired
	RoleRepository roleRepository;
	@Autowired
	TenancyAppRepository tenancyAppRepository;
	@Autowired
	TenancyCache tenancyCache;
	@Autowired
	AppCache appCache;

	// @Autowired
	// UserRepository userRepository;

	public boolean canDelete(UapApp uapApp) {
		List<UapCode> codes = codeRepository.findByUapApp(uapApp);
		if (codes != null && codes.size() > 0) {
			return false;
		}
		List<UapMenu> menus = menuRepository.findByUapApp(uapApp);
		if (menus != null && menus.size() > 0) {
			return false;
		}
		List<UapRestApi> apis = restApiRepository.findByUapApp(uapApp);
		if (apis != null && apis.size() > 0) {
			return false;
		}
		List<UapRole> roles = roleRepository.findByUapApp(uapApp);
		if (roles != null && roles.size() > 0) {
			return false;
		}
		return true;
	}

	public void checkAppAuthority(SessionUser sessionUser, List<Long> targetAppIds) {
		// if (UapConstant.APP_TYPE_BIZ.equals(sessionUser.getAppType())) {
		// List<UapApp> operateApps =
		// findByIdOrParentId(sessionUser.getAppId());
		// List<Long> operateAppIds = new ArrayList<>();
		// for (UapApp operateApp : operateApps) {
		// operateAppIds.add(operateApp.getId());
		// }
		// for (Long targetAppId : targetAppIds) {
		// if (!operateAppIds.contains(targetAppId)) {
		// throw new UAPException(ResponseCode.ERR_FORBIDDEN_CROSS_APP);
		// }
		// }
		// }
	}

	public void checkAppAuthority(SessionUser sessionUser, Long targetAppId) {
		List<Long> targetAppIds = new ArrayList<>();
		targetAppIds.add(targetAppId);
		// checkAppAuthority(sessionUser, targetAppIds);
	}

	public String delete(UapApp app) {
		if (!canDelete(app)) {
			return ResponseCode.DATA_IN_USE;
		}
		Long appId = app.getId();
		appRepository.delete(app);
		appCache.delete(appId);
		List<UapTenancyApp> uapTenancyAppList = tenancyAppRepository.findByAppId(appId);
		if (null != uapTenancyAppList && uapTenancyAppList.size()>=1){
			for (UapTenancyApp uapTenancyApp:uapTenancyAppList){
				tenancyCache.put(uapTenancyApp.getTanancyId());
			}
			tenancyAppRepository.deleteByAppId(appId);
		}
		return ResponseCode.OPERATE_SUCCESS;
	}

	public PageData<UapApp> findApps(AppQuery query) {
		return appCustomRepository.findApps(query);
	}

	public UapApp findByCode(String appCode) {
		List<UapApp> res = appRepository.findByCode(appCode);
		if (res != null && res.size() == 1) {
			return res.get(0);
		}
		return null;
	}

	public List<UapApp> findByIdOrParentId(Long appId) {
		return appRepository.findByIdOrParentId(appId, appId);
	}

	public UapApp get(Long id) {
		if (StringUtils.isEmpty(id)) {
			return null;
		}
		UapApp result = appCache.get(id);
		if (StringUtils.isEmpty(result)) {
			Optional<UapApp> res = appRepository.findById(id);
			if (null == res) {
				return null;
			}
			if (res.isPresent()) {
				appCache.put(res.get());
				return res.get();
			}
		}
		return result;
	}

	@Transactional
	public String save(UapApp entity, Long tenancyId) {
		List<UapApp> apps = appRepository.findByCode(entity.getCode());
		if (!CollectionUtils.isEmpty(apps)) {
			return ResponseCode.CODE_EXIST;
		}
		apps = appRepository.findByName(entity.getName());
		if (!CollectionUtils.isEmpty(apps)) {
			return ResponseCode.NAME_EXIST;
		}
		entity.setInTime(System.currentTimeMillis());
		entity.setState(UapConstant.UAP_COMM_ENABLED);
		entity.setUpTime(System.currentTimeMillis());
		UapApp res = appRepository.save(entity);
		if (res == null) {
			return ResponseCode.OPERATE_FAILURE;
		}
		appCache.put(res);
		// 新增保存成功后执行初始化数据
		// appInitService.initData(res, userNo, password, orgName);
		// 新增应用添加租户关联关系
		UapTenancyApp uapTenancyApp = new UapTenancyApp();
		uapTenancyApp.setTanancyId(tenancyId);
		uapTenancyApp.setAppId(res.getId());
		tenancyAppRepository.save(uapTenancyApp);
		tenancyCache.put(tenancyId);
		return ResponseCode.OPERATE_SUCCESS;
	}

	public String update(UapApp entity) {
		// code重复校验
		List<UapApp> apps = appRepository.findByCodeAndIdNot(entity.getCode(), entity.getId());
		if (!CollectionUtils.isEmpty(apps)) {
			return ResponseCode.CODE_EXIST;
		}
		// name重复校验
		apps = appRepository.findByNameAndIdNot(entity.getName(), entity.getId());
		if (!CollectionUtils.isEmpty(apps)) {
			return ResponseCode.NAME_EXIST;
		}

		entity.setUpTime(System.currentTimeMillis());
		UapApp res = appRepository.save(entity);
		if (res == null) {
			return ResponseCode.OPERATE_FAILURE;
		}
		appCache.put(res);
		return ResponseCode.OPERATE_SUCCESS;
	}
}
