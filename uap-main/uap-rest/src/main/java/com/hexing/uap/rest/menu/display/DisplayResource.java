package com.hexing.uap.rest.menu.display;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.hexing.uap.bean.jpa.UapApp;
import com.hexing.uap.service.AppService;
import com.hexing.uap.util.ConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hexing.uap.bean.custom.MenuResponse;
import com.hexing.uap.bean.jpa.UapMenu;
import com.hexing.uap.bean.jpa.UapMenuDisplay;
import com.hexing.uap.bean.jpa.UapMenuLocal;
import com.hexing.uap.common.UapConstant;
import com.hexing.uap.common.bean.CommonResponse;
import com.hexing.uap.common.bean.ListDataResponse;
import com.hexing.uap.common.bean.ModelResponse;
import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.repository.param.MenuDisplayQuery;
import com.hexing.uap.service.MenuDisplayService;
import com.hexing.uap.service.MenuLocalService;
import com.hexing.uap.service.MenuService;
import com.hexing.uap.util.LocaleUtil;
import com.hexing.uap.util.ValidatortUtil;
import com.hexing.uap.web.SessionUserUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * Created by Administrator on 2018/12/20 0020.
 */

@RestController
@Api(tags = { "MenuDisplay" }, description = "Manage Menu Display", authorizations = { @Authorization(value = "basicAuth") })
public class DisplayResource {

    @Autowired
    MenuDisplayService service;
    @Autowired
    MenuService menuService;
    @Autowired
    MenuLocalService menuLocalService;
    @Autowired
    private AppService appService;

    @ApiOperation(notes = "", value = "Get MenuDisplay By MenuDisplayId", tags = { "MenuDisplay" })
    @GetMapping(value = "/menuDisplay/{menuDisplayId}", produces = "application/json")
    public ModelResponse<MenuDisplayResponse> get(
            @ApiParam(name = "menuDisplayId", value = "menuDisplay's id(not null,String)") @PathVariable String menuDisplayId) {

        ModelResponse<MenuDisplayResponse> response = new ModelResponse<>();
        UapMenuDisplay uapMenuDisplay = null;
        if (menuDisplayId != null) {
            uapMenuDisplay = service.get(menuDisplayId);
        }
        if (uapMenuDisplay == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            response.setData(null);
            return response;
        }
        MenuDisplayResponse obj = new MenuDisplayResponse();
        obj.setCode(uapMenuDisplay.getCode());
        obj.setId(uapMenuDisplay.getId());
        obj.setInsertTime(uapMenuDisplay.getInsertTime());
        obj.setMenuId(uapMenuDisplay.getMenuId());
        UapMenu uapMenu = menuService.get(uapMenuDisplay.getMenuId());
        if (null != uapMenu){
            List<UapMenuDisplay> uapMenuDisplays = service.findByMenuId(uapMenuDisplay.getMenuId());
            String[] dis = menuDisplayId.split("-");
            String groupId = dis[0];
            for (UapMenuDisplay error:uapMenuDisplays){
                if (error.getId().startsWith(groupId) && error.getId() !=menuDisplayId){
                    response.setMsgCode(ResponseCode.SQL_DATA_ERROR);
                    return response;
                }
            }
            obj.setUapMenu(uapMenu);
        }
        obj.setName(uapMenuDisplay.getName());
        obj.setParentId(uapMenuDisplay.getParentId());
        obj.setRankId(uapMenuDisplay.getRankId());
        obj.setState(uapMenuDisplay.getState());
        obj.setType(uapMenuDisplay.getType());
        obj.setAppId(uapMenuDisplay.getAppId());
        obj.setAppName(uapMenuDisplay.getAppName());
        obj.setIcon(uapMenuDisplay.getIcon());
        obj.setSize(uapMenuDisplay.getMenuSize());
        obj.setUpdateTime(uapMenuDisplay.getUpdateTime());
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        response.setData(obj);
        return response;
    }

    @ApiOperation(notes = "", value = "Creat New MenuDisplay", tags = { "MenuDisplay" })
    @PostMapping(value = "/menuDisplay", produces = "application/json")
    public CommonResponse save(@RequestBody MenuDisplayCreateRequest param) {
        CommonResponse response = new CommonResponse();
        ValidatortUtil.validate(param);

        UapMenuDisplay uapMenuDisplay = new UapMenuDisplay();
        UapMenuDisplay idExit = service.get(param.getDisplayId());
        if (null != idExit){
            response.setMsgCode(ResponseCode.ID_EXIT);
            return response;
        }
        uapMenuDisplay.setId(param.getDisplayId());
        uapMenuDisplay.setCode(param.getCode());
        uapMenuDisplay.setInsertTime(System.currentTimeMillis());
        uapMenuDisplay.setName(param.getName());
        uapMenuDisplay.setParentId(param.getParentId());
        List<UapMenuDisplay> rankExit = service.findByParentIdAndRankId(param.getParentId(), param.getRankId());
        if (null != rankExit && rankExit.size()>=1){
            response.setMsgCode(ResponseCode.RANK_ID_EXIT);
            return response;
        }
        uapMenuDisplay.setRankId(param.getRankId());
        uapMenuDisplay.setState(param.getState());
        uapMenuDisplay.setType(param.getType());
        uapMenuDisplay.setIcon(param.getIcon());
        uapMenuDisplay.setMenuSize(param.getSize());
        uapMenuDisplay.setUpdateTime(System.currentTimeMillis());
        UapMenuDisplay res = service.save(uapMenuDisplay);
        if (null == res){
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "Uapdate MenuDisplay Infomation", tags = {"MenuDisplay"}, notes = "")
    @PutMapping(value = "/menuDisplay/{menuDisplayId}", produces = "application/json")
    public CommonResponse update(@ApiParam(name = "menuDisplayId", value = "menuDisplay's id") @PathVariable String menuDisplayId,
                                 @RequestBody MneuDisplayUpdateRequest param) {
        CommonResponse response = new CommonResponse();
        ValidatortUtil.validate(param);
        if (null == menuDisplayId) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        UapMenuDisplay uapMenuDisplay = service.get(menuDisplayId);
        if (uapMenuDisplay == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }

        uapMenuDisplay.setName(param.getName());
        uapMenuDisplay.setCode(param.getCode());
        List<UapMenuDisplay> rankExit = service.findByParentIdAndRankId(uapMenuDisplay.getParentId(), param.getRankId());
        
        if (null != rankExit && rankExit.size()>=1){
        	UapMenuDisplay tmp = rankExit.get(0);
        	
        	if(tmp!= null && !tmp.getId().equals(uapMenuDisplay.getId())){
        		  response.setMsgCode(ResponseCode.RANK_ID_EXIT);
                  return response;
        	}
          
        }
        uapMenuDisplay.setRankId(param.getRankId());
        uapMenuDisplay.setState(param.getState());
        uapMenuDisplay.setIcon(param.getIcon());
        uapMenuDisplay.setMenuSize(param.getSize());
        uapMenuDisplay.setUpdateTime(System.currentTimeMillis());
        UapMenuDisplay res = service.save(uapMenuDisplay);
        if (null == res){
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(notes = "", value = "Delete MenuDisplay", tags = { "MenuDisplay" })
    @DeleteMapping(value = "/menuDisplay/{menuDisplayId}", produces = "application/json")
    public CommonResponse delete(
            @ApiParam(name = "menuDisplayId", value = "menuDisplay's id(not null,String)") @PathVariable String menuDisplayId) {
        CommonResponse response = new CommonResponse();
        if (menuDisplayId == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        UapMenuDisplay uapMenuDisplay = service.get(menuDisplayId);
        if (uapMenuDisplay == null) {
            response.setMsgCode(ResponseCode.NOT_EXIST_DATA);
            return response;
        }
        List<UapMenuDisplay> uapMenuDisplays = service.findByParentId(menuDisplayId);
        if (null !=uapMenuDisplays && uapMenuDisplays.size() !=0){
            response.setMsgCode(ResponseCode.DATA_IN_USE);
            return response;
        }
        String code = service.delete(uapMenuDisplay);
        response.setMsgCode(code);
        return response;
    }

    @ApiOperation(value = "Get MenuDisplay By ParentId", tags = { "MenuDisplay" })
    @PostMapping(value = "/menuDisplay/sublist", produces = "application/json")
    public ListDataResponse<MenuDisplayResponse> findSubMenuDisplaies(
                                                       @RequestBody MenuDisplayTreeRequest requestParam) {
        ListDataResponse<MenuDisplayResponse> response = new ListDataResponse<>();
        ValidatortUtil.validate(requestParam);
        String language = SessionUserUtil.getLocal();
        MenuDisplayQuery queryBean = new MenuDisplayQuery();
        String parentId = requestParam.getParentId();
        queryBean.setState(requestParam.getState());
        queryBean.setUnState(requestParam.getUnState());
        queryBean.setParentId(parentId);
        List<UapMenuDisplay> menus = service.findSubMenuDisplies(queryBean);
        List<UapMenuDisplay> results = new ArrayList<>();
        transferBylanguage(results,menus,language);
        List<MenuDisplayResponse> menuResponses =getResponseByList(menus);
        response.setData(menuResponses);
        response.setTotal(menuResponses.size());
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    private String getDisplayName(UapMenuDisplay menuDisplay, String language) {
        List<UapMenuLocal> locals = menuLocalService.findByObjIdAndType(menuDisplay.getId(),UapConstant.MENU_LOCAL_TYPE_DISPLAY);
        Iterator<UapMenuLocal> it = locals.iterator();
        String displayName = menuDisplay.getName();
        if (language != null) {
            while (it.hasNext()) {
                UapMenuLocal local = it.next();
                if (local != null && language.equals(local.getLocal())) {
                    if (!StringUtils.isEmpty(local.getDisplayName())) {
                        displayName = local.getDisplayName();
                    }
                }
            }
        }
        return displayName;
    }

    private void transferBylanguage(List<UapMenuDisplay> results, List<UapMenuDisplay> uapMenuDisplays, String language) {
        if (uapMenuDisplays == null || uapMenuDisplays.isEmpty()) {
            return;
        }
        for (UapMenuDisplay menuDisplay : uapMenuDisplays) {
            String displayName = getDisplayName(menuDisplay, language);
            menuDisplay.setName(displayName);
            results.add(menuDisplay);
        }
    }

    @ApiOperation(value = "Get MenuDisplay List", tags = { "MenuDisplay" })
    @PostMapping(value = "/menuDisplay/list", produces = "application/json")
    public ListDataResponse<MenuDisplayResponse> findMenuDisplaies(
                                                                      @RequestBody MenuDisplayListRequest requestParam) {
        ListDataResponse<MenuDisplayResponse> response = new ListDataResponse<>();
        ValidatortUtil.validate(requestParam);
        String type = requestParam.getType();
        List<UapMenuDisplay> menus = service.findMenuDispliesByType(type);
        List<MenuDisplayResponse> menuResponses =getResponseByList(menus);

        response.setData(menuResponses);
        response.setTotal(menuResponses.size());
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }


    @ApiOperation(value = "relevance MenuDisplay and menu", tags = {"MenuDisplay"}, notes = "")
    @PostMapping(value = "/menuDisplay/{menuDisplayId}/menu", produces = "application/json")
    public CommonResponse relevance(@ApiParam(name = "menuDisplayId", value = "menuDisplay's id") @PathVariable String menuDisplayId,
                                 @RequestBody MneuDisplayMenuRequest param) {
        CommonResponse response = new CommonResponse();
        ValidatortUtil.validate(param);
        if (null == menuDisplayId) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        UapMenuDisplay uapMenuDisplay = service.get(menuDisplayId);
        //只有菜单才可关联
        if (null == uapMenuDisplay || !uapMenuDisplay.getType().equals(UapConstant.MENU_DISPLAY_TYPE_MENU)) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        UapMenu uapMenu = menuService.get(param.getMenuId());
        if (null == uapMenu){
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        uapMenuDisplay.setAppId(uapMenu.getUapApp().getId());
        uapMenuDisplay.setAppName(uapMenu.getUapApp().getName());
        uapMenuDisplay.setMenuId(param.getMenuId());
        uapMenuDisplay.setUpdateTime(System.currentTimeMillis());
        //权限修改，先查询是否已经具有关联菜单，有则同步去掉角色菜单关联关系
        UapMenuDisplay res = service.save(uapMenuDisplay);
        if (null == res){
            response.setMsgCode(ResponseCode.OPERATE_FAILURE);
            return response;
        }
        List<UapMenuLocal> uapMenuLocalList = menuLocalService.findByObjIdAndType(param.getMenuId(),UapConstant.MENU_LOCAL_TYPE_MENU);
        for (UapMenuLocal uapMenuLocal:uapMenuLocalList){
            UapMenuLocal displayLocal = new UapMenuLocal();
            displayLocal.setType(UapConstant.MENU_LOCAL_TYPE_DISPLAY);
            displayLocal.setObjId(res.getId());
            displayLocal.setDisplayName(uapMenuLocal.getDisplayName());
            displayLocal.setLocal(uapMenuLocal.getLocal());
            menuLocalService.save(displayLocal);
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "relevance MenuDisplayGroup and menus", tags = {"MenuDisplay"}, notes = "")
    @PostMapping(value = "/menuDisplay/{menuDisplayId}/menus", produces = "application/json")
    public CommonResponse relevanceMenus(@ApiParam(name = "menuDisplayId", value = "menuDisplay's id") @PathVariable String menuDisplayId,
                                    @RequestBody MneuDisplayMenusRequest param) {
        CommonResponse response = new CommonResponse();
        UapMenuDisplay uapMenuDisplay = service.get(menuDisplayId);
        if (null == uapMenuDisplay) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        //此接口要求所传menuDisplayId必须为group类型
        String type = uapMenuDisplay.getType();
        if (!type.equals(UapConstant.MENU_DISPLAY_TYPE_GROUP) && !type.equals(UapConstant.MENU_DISPLAY_TYPE_SET)){
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        List<UapMenu> uapMenuList = param.getUapMenuList();
        if (null != uapMenuList){
            for (UapMenu uapMenu:uapMenuList){
                UapMenuDisplay display = new UapMenuDisplay();
                if (null != uapMenu.getId()){
                    UapMenu menu = menuService.get(uapMenu.getId());
                    String random = UUID.randomUUID().toString();
                    String uuid = StringUtils.replace(random.toString(), "-", "");
                    String displayId =menuDisplayId +"-"+ uuid;
                    display.setId(displayId);
                    display.setMenuId(menu.getId());
                    display.setCode(menu.getCode());
                    display.setUpdateTime(System.currentTimeMillis());
                    display.setType(UapConstant.MENU_DISPLAY_TYPE_MENU);
                    display.setParentId(menuDisplayId);
                    display.setState(menu.getState());
                    display.setName(menu.getName());
                    display.setInsertTime(System.currentTimeMillis());
                    display.setAppId(menu.getUapApp().getId());
                    display.setAppName(menu.getUapApp().getName());
                    UapMenuDisplay res = service.save(display);
                    if (null == res){
                        response.setMsgCode(ResponseCode.OPERATE_FAILURE);
                        return response;
                    }
                    List<UapMenuLocal> uapMenuLocalList = menuLocalService.findByObjIdAndType(uapMenu.getId(),UapConstant.MENU_LOCAL_TYPE_MENU);
                    for (UapMenuLocal uapMenuLocal:uapMenuLocalList){
                        UapMenuLocal displayLocal = new UapMenuLocal();
                        displayLocal.setType(UapConstant.MENU_LOCAL_TYPE_DISPLAY);
                        displayLocal.setObjId(res.getId());
                        displayLocal.setDisplayName(uapMenuLocal.getDisplayName());
                        displayLocal.setLocal(uapMenuLocal.getLocal());
                        menuLocalService.save(displayLocal);
                    }
                }
            }
        }
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    public List<MenuDisplayResponse> getResponseByList(List<UapMenuDisplay> menus){
        List<MenuDisplayResponse> menuResponses = new ArrayList<>();
        for(UapMenuDisplay uapMenuDisplay: menus) {
            MenuDisplayResponse obj = new MenuDisplayResponse();
            obj.setUpdateTime(uapMenuDisplay.getUpdateTime());
            obj.setType(uapMenuDisplay.getType());
            obj.setState(uapMenuDisplay.getState());
            obj.setRankId(uapMenuDisplay.getRankId());
            obj.setParentId(uapMenuDisplay.getParentId());
            obj.setName(uapMenuDisplay.getName());
            obj.setMenuId(uapMenuDisplay.getMenuId());
            obj.setInsertTime(uapMenuDisplay.getInsertTime());
            obj.setId(uapMenuDisplay.getId());
            obj.setCode(uapMenuDisplay.getCode());
            obj.setSize(uapMenuDisplay.getMenuSize());
            menuResponses.add(obj);
        }
        return menuResponses;
    }
    
    
    @ApiOperation(notes = "", value = "Get Menudisplay Info By MenuId", tags = { "MenuDisplay" })
    @PostMapping(value = "/menuDisplay/menu/{menuId}", produces = "application/json")
    public ModelResponse<MenuResponse> findMenuByMenuId(HttpServletRequest httpRequest,
            @ApiParam(name = "menuId", value = "menu's id(not null,String)") @PathVariable String menuId,
                                                           @RequestBody MneuInfoRequest param) {
        ModelResponse<MenuResponse> response = new ModelResponse<>();
        ValidatortUtil.validate(param);
        if (StringUtils.isEmpty(menuId)) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            response.setData(null);
            return response;
        }
        UapMenu uapMenu = menuService.get(menuId);
        if (null == uapMenu){
            response.setMsgCode(ResponseCode.ERR_PARAM);
            response.setData(null);
            return response;
        }
        String configId = param.getConfigId();
        List<UapMenuDisplay> menuDisplayList = service.findByMenuId(menuId);
        String menuDisplayId = null;
        if (null !=menuDisplayList && menuDisplayList.size()>=1){
            for (UapMenuDisplay uapMenuDisplay:menuDisplayList){
                if (uapMenuDisplay.getId().startsWith(configId)){
                    menuDisplayId = uapMenuDisplay.getId();
                }
            }
        }
        MenuResponse menu = ConvertUtil.convert(uapMenu, MenuResponse.class);
        if (null != menuDisplayId){
            Locale locale = LocaleUtil.getLocale(httpRequest);
            String language = LocaleUtil.getLocaleString(locale);
            List<UapMenuLocal> locals = menuLocalService.findByObjIdAndType(menuDisplayId,
                    UapConstant.MENU_LOCAL_TYPE_DISPLAY);
            for (UapMenuLocal uapMenuLocal : locals) {
                if (Objects.equals(language, uapMenuLocal.getLocal())) {
                    menu.setName(uapMenuLocal.getDisplayName());
                    break;
                }
            }
            menu.setId(menuDisplayId);
        }else {
            response.setMsgCode(ResponseCode.NOT_EXIST_DATA);
            return response;
        }
        response.setData(menu);
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "get MenuDisplay relete apps", tags = {"MenuDisplay"}, notes = "")
    @PostMapping(value = "/menuDisplay/{menuDisplayId}/apps", produces = "application/json")
    public ListDataResponse<UapApp> getMenudisplayApps(@ApiParam(name = "menuDisplayId", value = "menuDisplay's id") @PathVariable String menuDisplayId) {
        ListDataResponse<UapApp> response = new ListDataResponse();
        UapMenuDisplay uapMenuDisplay = service.get(menuDisplayId);
        if (null == uapMenuDisplay) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        List<UapApp> uapAppList = new ArrayList<>();
        List<Long> appIds = service.findApps(menuDisplayId);
        if (null != appIds && appIds.size()>=1){
            for (Long appId:appIds){
                UapApp uapApp = appService.get(appId);
                if (null != uapApp){
                    uapAppList.add(uapApp);
                }
            }
        }
        response.setData(uapAppList);
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(notes = "", value = "Delete MenuDisplay App By AppId", tags = { "MenuDisplay" })
    @DeleteMapping(value = "/menuDisplay/{menuDisplayId}/app", produces = "application/json")
    public CommonResponse deleteMenudisplayApp(
            @ApiParam(name = "menuDisplayId", value = "menuDisplay's id(not null,String)") @PathVariable String menuDisplayId,
            @RequestBody MneuDisplayAppDeleteRequest param) {
        CommonResponse response = new CommonResponse();
        if (menuDisplayId == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        UapMenuDisplay uapMenuDisplay = service.get(menuDisplayId);
        if (uapMenuDisplay == null) {
            response.setMsgCode(ResponseCode.NOT_EXIST_DATA);
            return response;
        }
        ValidatortUtil.validate(param);
        String appId = param.getAppId();
        String code = service.deleteMenuDisplayApp(appId);
        response.setMsgCode(code);
        return response;
    }
}
