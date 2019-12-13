package com.hexing.uap.rest.user.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hexing.uap.bean.jpa.UapApp;
import com.hexing.uap.bean.jpa.UapCode;
import com.hexing.uap.bean.jpa.UapMenu;
import com.hexing.uap.bean.jpa.UapMenuDisplay;
import com.hexing.uap.bean.jpa.UapMenuLocal;
import com.hexing.uap.common.UapConstant;
import com.hexing.uap.common.bean.ListDataResponse;
import com.hexing.uap.common.bean.ModelResponse;
import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.message.MenuResponseCode;
import com.hexing.uap.message.UserResponseCode;
import com.hexing.uap.repository.param.MenuQuery;
import com.hexing.uap.rest.menu.MenuTreeNode;
import com.hexing.uap.rest.menu.display.MenuDisplayResponse;
import com.hexing.uap.service.CodeService;
import com.hexing.uap.service.MenuDisplayService;
import com.hexing.uap.service.MenuLocalService;
import com.hexing.uap.service.MenuService;
import com.hexing.uap.service.TenancyAppService;
import com.hexing.uap.util.ValidatortUtil;
import com.hexing.uap.web.SessionUserUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;

@RestController
@Api(tags = { "Users" }, authorizations = { @Authorization(value = "basicAuth") })
public class UserMenuResource {

	@Autowired
	private MenuService menuService;
	@Autowired
	CodeService codeService;
//	@Autowired
//	private UserFavoriteMenuService userFavouriteService;
	@Autowired
	MenuLocalService menuLocalService;
	@Autowired
	TenancyAppService tenancyAppService;
	@Autowired
	MenuDisplayService menuDisplayService;

	/**
	 * 获取页面所需的权限和编码资源
	 *
	 * @param httpRequest
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取页面所需的权限和编码资源", nickname = "listFunctionAndCode", tags = { "Users" })
	@PostMapping(value = "/user/function/code", produces = "application/json")
	public ModelResponse<UserFunctionResponse> getFunction(HttpServletRequest httpRequest,
			@RequestBody UserFunctionQueryRequest request) {
		ModelResponse<UserFunctionResponse> response = new ModelResponse<>();
		ValidatortUtil.validate(request);
		String language = request.getLanguage();
		if (language == null) {
			language = SessionUserUtil.getLocal();
		}
		Long appId = request.getAppId();
		String displayParentId = request.getMenuParentId();
		UserFunctionResponse data = new UserFunctionResponse();
		UapMenuDisplay uapMenuParentDisplay = menuDisplayService.get(displayParentId);
		String menuParentId = null;
		if (null != uapMenuParentDisplay){
			menuParentId = uapMenuParentDisplay.getMenuId();
			List<String> datas = menuService.findFunction(SessionUserUtil.getUserId(), menuParentId, appId);
			List<MenuTreeNode> nodes = new ArrayList<>();
			for (String code:datas){
				if(StringUtils.isEmpty(code)){
					continue;
				}
				MenuTreeNode node = new MenuTreeNode();
				node.setCode(code);
				nodes.add(node);
			}
			data.setFunctions(nodes);
		}
		List<String> parentCodes = request.getParentCodes();
		if (parentCodes != null && parentCodes.size() > 0) {
			HashMap<String, List<UapCode>> codes = codeService.findByParentCodes(parentCodes, appId,language);
			data.setCodes(codes);
		}
		response.setData(data);
		response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
		return response;
	}

	/**
	 * 同步或异步方式查询用户有权访问的菜单
	 *
	 * @param httpRequest
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "同步或异步方式查询用户有权访问的菜单", nickname = "listUserMenus", tags = { "Users" })
	@PostMapping(value = "/user/menus", produces = "application/json")
	public ListDataResponse<MenuTreeNode> getByUser(HttpServletRequest httpRequest,
			@RequestBody UserMenuQueryRequest request) {
		ListDataResponse<MenuTreeNode> response = new ListDataResponse<>();
		String language = request.getLanguage();
		if (language == null) {
			language = SessionUserUtil.getLocal();
		}
		Long tenancyId = SessionUserUtil.getTenancyId();
		String diplayId = SessionUserUtil.getSessionUser().getMenuDisPlayId();
		//未配置菜单组合
		if (null == diplayId || StringUtils.isEmpty(diplayId)){
			response.setMsgCode(UserResponseCode.MENUDISPLAY_NOT_SET);
			response.setData(null);
			return response;
		}
		List<UapApp> apps = tenancyAppService.findAppsByTenancyId(tenancyId);
		if (apps == null || apps.isEmpty()) {
			response.setMsgCode(MenuResponseCode.MENU_NO_SET_APP);
			return response;
		}
		
		MenuQuery queryBean = new MenuQuery();
		queryBean.setParentId(request.getParentId());
		queryBean.setAppId(request.getAppId());
		queryBean.setUserId(request.getUserId());
		queryBean.setSynchronize(request.isSynchronize());
		queryBean.setLimitType(request.getLimitType());

		List<UapMenu> menuList = menuService.findByUser(queryBean);
		List<UapMenu> menus = new ArrayList<>();
		for (UapMenu uapMenu:menuList){
			if (uapMenu.getType().equals(UapConstant.MENU_TYPE_MENU)){
				menus.add(uapMenu);
			}
		}
		List<MenuDisplayResponse> menuDisplayResponseListAll = getMenuResponseListByMenuList(menus,diplayId);
		//递归找到所有父菜单并标记
		for (MenuDisplayResponse menuDisplayResponse:menuDisplayResponseListAll){
			if (null != menuDisplayResponse && null != menuDisplayResponse.getType() &&menuDisplayResponse.getType().equals(UapConstant.MENU_DISPLAY_TYPE_GROUP)){
				markDisplayMenuGroup(menuDisplayResponseListAll,menuDisplayResponse.getId(),menuDisplayResponse);
			}
		}
		//获取全部菜单定制国际化信息
		List<UapMenuLocal> allLocals = menuLocalService.findAllDisplayLocalById(diplayId, UapConstant.MENU_LOCAL_TYPE_DISPLAY,language);
		HashMap<String,String> map = new HashMap<>();
		for (UapMenuLocal uapMenuLocal:allLocals){
			map.put(uapMenuLocal.getObjId(),uapMenuLocal.getDisplayName());
		}
		//循环去掉没有子菜单的分组
		List<MenuDisplayResponse> menuDisplayResponseList = getDisplayMenus(menuDisplayResponseListAll);
		List<MenuTreeNode> results = new ArrayList<>();
		if (UapConstant.MENU_TYPE_MENU.equals(request.getLimitType())) {
			for (MenuDisplayResponse m : menuDisplayResponseList) {
				MenuTreeNode n = getTreeNode(null, m, map);
				results.add(n);
			}
		} else {

			results = getMenus(apps, menuDisplayResponseList, map,diplayId);
		}
		response.setData(results);
		response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
		return response;
	}



	private List<MenuTreeNode> geSubTreeNode(List<MenuTreeNode> nodes, String menuId) {
		List<MenuTreeNode> res = new ArrayList<>();
		for (MenuTreeNode node : nodes) {
			if (node.getParentId().equals(menuId)) {
				res.add(node);
			}
		}
		return res;
	}

	private void geSuTreeNode(List<MenuTreeNode> firstNodes, List<MenuTreeNode> allNodes, List<MenuTreeNode> res, String displayId) {
		for (MenuTreeNode node : firstNodes) {
			String type = node.getType();
			// 只有菜单分组才有下级
			if (UapConstant.MENU_DISPLAY_TYPE_GROUP.equals(type)) {
				// 获取分组的下级
				List<MenuTreeNode> subTmp = geSubTreeNode(allNodes, node.getId());
				if (subTmp.size() > 0) {
					node.setChildren(subTmp);
					geSuTreeNode(subTmp, allNodes, res, displayId);
				}
				// 加入节点
				if (node.getParentId().equals(displayId)) {
					res.add(node);
				}
			} else if (UapConstant.MENU_DISPLAY_TYPE_MENU.equals(type)) {
				// 无父节点菜单作为根节点
				if (node.getParentId().equals(displayId)) {
					res.add(node);
				}
			}
		}
	}

//	private String getDisplayName(MenuDisplayResponse menu, String language) {
//	    //这里的调用只是给user/menus查询定制的国际化信息
//		List<UapMenuLocal> locals = menuLocalService.findByObjIdAndType(menu.getId(), UapConstant.MENU_LOCAL_TYPE_DISPLAY);
//		Iterator<UapMenuLocal> it = locals.iterator();
//		String displayName = menu.getName();
//		if (language != null) {
//			while (it.hasNext()) {
//				UapMenuLocal local = it.next();
//				if (local != null && language.equals(local.getLocal())) {
//					if (!StringUtils.isEmpty(local.getDisplayName())) {
//						displayName = local.getDisplayName();
//					}
//				}
//			}
//		}
//
//		return displayName;
//	}

	private List<MenuTreeNode> getMenus(List<UapApp> apps, List<MenuDisplayResponse> menus, HashMap<String,String> map, String displayId) {
		List<MenuTreeNode> allNodes = new ArrayList<>();
		List<MenuTreeNode> firstNodes = new ArrayList<>();
		getTreeNode(apps, allNodes, firstNodes, menus, map,displayId);
		if (firstNodes == null || firstNodes.isEmpty()) {
			return allNodes;
		}
		List<MenuTreeNode> res = new ArrayList<>();
		geSuTreeNode(firstNodes, allNodes, res,displayId);
		return res;
	}

	private void getTreeNode(List<UapApp> apps, List<MenuTreeNode> allNodes, List<MenuTreeNode> firstNodes,
			List<MenuDisplayResponse> menus, HashMap<String,String> map, String displayId) {
		List<MenuTreeNode> nodes = new ArrayList<>();
		for (MenuDisplayResponse menu : menus) {
			MenuTreeNode node = getTreeNode(apps, menu, map);
			nodes.add(node);
			String type = menu.getType();
			if ((type.equals(UapConstant.MENU_DISPLAY_TYPE_GROUP)||type.equals(UapConstant.MENU_DISPLAY_TYPE_MENU))&&menu.getParentId().equals(displayId)) {
				firstNodes.add(node);
			}
			allNodes.add(node);
		}
	}

	private UapApp getApp(List<UapApp> apps, long appId) {
		for (UapApp app : apps) {
			if (app.getId().equals(appId)) {
				return app;
			}
		}
		return null;
	}

	private MenuTreeNode getTreeNode(List<UapApp> apps, MenuDisplayResponse menuResponse, HashMap<String,String> map) {
		String dispLayName = null;
		Object localName = map.get(menuResponse.getId());
		if (null != localName){
			dispLayName = localName.toString();
		}else {
			dispLayName = menuResponse.getName();
		}

		MenuTreeNode node = new MenuTreeNode();
		node.setCode(menuResponse.getCode());
		node.setId(menuResponse.getId());
		node.setIsLeaf(menuResponse.getType());
		node.setName(dispLayName);
		node.setParentId(menuResponse.getParentId());
		node.setType(menuResponse.getType());
		node.setIcon(menuResponse.getIcon());
		node.setMenuId(menuResponse.getMenuId());
		node.setSize(menuResponse.getSize());
		//查询code是用到，查询menus不需要
		if (menuResponse.getType().equals(UapConstant.MENU_DISPLAY_TYPE_MENU) && null !=menuResponse.getChildren()){
			node.setChildren(menuResponse.getChildren());
		}
		if (null!= menuResponse.getMenuId() && null != menuResponse.getUapMenu() && null != menuResponse.getUapMenu().getUapApp()){
			Long appId = menuResponse.getUapMenu().getUapApp().getId();
			UapApp app = menuResponse.getUapMenu().getUapApp();
			Long parentId = app.getParentId();
//			String appUrl = app.getUrl();
//			// 如果应用没有设置URL，且有上级，则获取上级的URL
//			if (StringUtils.isEmpty(appUrl) && parentId != null) {
//				UapApp parentApp = getApp(apps, parentId);
//				if (parentApp != null) {
//					appUrl = parentApp.getUrl();
//				}
//			}
			node.setAppId(appId);
//			node.setAppUrl(appUrl);
			node.setAppCode(app.getCode());
			node.setAppUiUrl(app.getUiUrl());
			node.setUrl(menuResponse.getUapMenu().getUrl());
			node.setUrlType(menuResponse.getUapMenu().getUrlType());
			node.setNo(menuResponse.getUapMenu().getNo());
			node.setFuncType(menuResponse.getUapMenu().getFuncType());
		}
		return node;
	}

//	private void transferBylanguage(List<UapCode> results, List<UapCode> cos, String language) {
//		if (cos == null || cos.isEmpty()) {
//			return;
//		}
//		for (UapCode co : cos) {
//			String displayName = getDisplayName(co, language);
//			co.setText(displayName);
//			results.add(co);
//		}
//	}

//	private String getDisplayName(UapCode co, String language) {
//		Set<UapCodeLocal> locals = co.getUapCodeLocals();
//		Iterator<UapCodeLocal> it = locals.iterator();
//		String displayName = co.getText();
//		if (language != null) {
//			while (it.hasNext()) {
//				UapCodeLocal local = it.next();
//				if (local != null && language.equals(local.getLocal())) {
//					if (!StringUtils.isEmpty(local.getDisplayName())) {
//						displayName = local.getDisplayName();
//					}
//				}
//			}
//		}
//		return displayName;
//	}

	public List<MenuDisplayResponse> getMenuResponseListByMenuList(List<UapMenu> menus, String diplayId){
		List<UapMenuDisplay> menuDisplayList = menuService.findByDisplayIdAndState(diplayId,UapConstant.UAP_ENABLED_DISPLAY);
		List<MenuDisplayResponse> menuDisplayResponseList = new ArrayList<>();
		for (UapMenuDisplay menuDisplay:menuDisplayList){
			String menuId = menuDisplay.getMenuId();
			//mark if have authration
			Boolean display = false;
			UapMenu menu = null;
			for (UapMenu uapMenu:menus){
				//父Id用做找到查询编码结果的父菜单
				if ( null != uapMenu.getType()){
					if (uapMenu.getId().equals(menuId)||(uapMenu.getType().equals(UapConstant.MENU_TYPE_FUNCTION)&&null != uapMenu.getParentId() && uapMenu.getParentId().equals(menuId))){
						display = true;
						menu = uapMenu;
					}
				}
			}
			String type = menuDisplay.getType();
			if (null != type &&(type.equals(UapConstant.MENU_DISPLAY_TYPE_GROUP)||(type.equals(UapConstant.MENU_DISPLAY_TYPE_MENU)&&display))){
				MenuDisplayResponse obj = new MenuDisplayResponse();
				obj.setCode(menuDisplay.getCode());
				obj.setId(menuDisplay.getId());
				obj.setInsertTime(menuDisplay.getInsertTime());
				obj.setMenuId(menuDisplay.getMenuId());
				if (null !=menu){
					obj.setUapMenu(menu);
				}
				obj.setName(menuDisplay.getName());
				obj.setParentId(menuDisplay.getParentId());
				obj.setRankId(menuDisplay.getRankId());
				obj.setState(menuDisplay.getState());
				obj.setType(menuDisplay.getType());
				obj.setUpdateTime(menuDisplay.getUpdateTime());
				obj.setIcon(menuDisplay.getIcon());
				obj.setSize(menuDisplay.getMenuSize());
				menuDisplayResponseList.add(obj);
			}
		}
		return menuDisplayResponseList;
	}
	public void markDisplayMenuGroup(List<MenuDisplayResponse> menuDisplayResponseList,String id,MenuDisplayResponse displayResponse){
		for (MenuDisplayResponse menuDisplayResponse:menuDisplayResponseList){
			if (menuDisplayResponse.getParentId().equals(id) && menuDisplayResponse.getType().equals(UapConstant.MENU_DISPLAY_TYPE_MENU)){
				displayResponse.setHaveChild(true);
			}else if (menuDisplayResponse.getParentId().equals(id) && menuDisplayResponse.getType().equals(UapConstant.MENU_DISPLAY_TYPE_GROUP)){
				markDisplayMenuGroup(menuDisplayResponseList,menuDisplayResponse.getId(),displayResponse);
			}
		}
	}

	public List<MenuDisplayResponse> getDisplayMenus(List<MenuDisplayResponse> menuDisplayResponseList){
		List<MenuDisplayResponse> menuDisplayResponses = new ArrayList<>();
		for (MenuDisplayResponse menuDisplayResponse:menuDisplayResponseList){
			if (menuDisplayResponse.getType().equals(UapConstant.MENU_DISPLAY_TYPE_MENU)){
				menuDisplayResponses.add(menuDisplayResponse);
			}else if (menuDisplayResponse.getType().equals(UapConstant.MENU_DISPLAY_TYPE_GROUP)&& null !=menuDisplayResponse.getHaveChild() &&menuDisplayResponse.getHaveChild()){
				menuDisplayResponses.add(menuDisplayResponse);
			}
		}
		return menuDisplayResponses;
	}
}
