package com.hexing.uap.rest.tenancy.app;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hexing.uap.bean.jpa.UapApp;
import com.hexing.uap.bean.jpa.UapMultiTenancy;
import com.hexing.uap.bean.jpa.UapTenancyApp;
import com.hexing.uap.common.bean.CommonResponse;
import com.hexing.uap.common.bean.ListDataResponse;
import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.service.AppService;
import com.hexing.uap.service.MultiTenancyService;
import com.hexing.uap.service.TenancyAppService;
import com.hexing.uap.util.ValidatortUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * Created by Administrator on 2018/12/6 0006.
 */


@RestController
@Api(tags = { "TenancyApp" }, description = "Manage Tenancy And App", authorizations = { @Authorization(value = "basicAuth") })
public class TenancyAppResource {

    @Autowired
    TenancyAppService service;
    @Autowired
    MultiTenancyService tenancyService;
    @Autowired
    private AppService appService;

    @ApiOperation(notes = "", value = "添加租户关联应用", tags = { "TenancyApp" })
    @PostMapping(value = "/tenancyApp/app/{tenancyId}", produces = "application/json")
    public CommonResponse saveTenancies(@RequestBody TanancyAppCreateRequest param,
                               @ApiParam(name = "tenancyId", value = "tenancy's id(not null,Long)") @PathVariable Long tenancyId) {
        CommonResponse response = new CommonResponse();
        ValidatortUtil.validate(param);

        //check tenancy and app
        UapMultiTenancy uapMultiTenancy = tenancyService.get(tenancyId);
        if (uapMultiTenancy == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        service.deleteByTenancyId(tenancyId);
        if (null == param.getAppIdList()){
            response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
            return response;
        }
        for (Long appId:param.getAppIdList()) {
            UapApp uapApp = appService.get(appId);
            if (uapApp == null) {
                response.setMsgCode(ResponseCode.ERR_PARAM);
                return response;
            }
            UapTenancyApp uapTenancyApp = new UapTenancyApp();
            uapTenancyApp.setAppId(appId);
            uapTenancyApp.setTanancyId(tenancyId);
            String code = service.save(uapTenancyApp);
            response.setMsgCode(code);
            if (code.equals(ResponseCode.OPERATE_FAILURE)){
                return response;
            };
        }
        return response;
    }

    @ApiOperation(notes = "", value = "添加应用关联租户", tags = { "TenancyApp" })
    @PostMapping(value = "/tenancyApp/tenancy/{appId}", produces = "application/json")
    public CommonResponse saveApps(@RequestBody AppTanancyCreateRequest param,
                               @ApiParam(name = "appId", value = "app's id(not null,Long)") @PathVariable Long appId) {
        CommonResponse response = new CommonResponse();
        ValidatortUtil.validate(param);

        //check tenancy and app
        UapApp uapApp = appService.get(appId);
        if (uapApp == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        service.deleteByAppId(appId);
        if (null == param.getTenancyIdList()){
            response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
            return response;
        }
        for (Long tenancyId:param.getTenancyIdList()) {
            UapMultiTenancy uapMultiTenancy = tenancyService.get(tenancyId);
            if (uapMultiTenancy == null) {
                response.setMsgCode(ResponseCode.ERR_PARAM);
                return response;
            }
            UapTenancyApp uapTenancyApp = new UapTenancyApp();
            uapTenancyApp.setAppId(appId);
            uapTenancyApp.setTanancyId(tenancyId);
            String code = service.save(uapTenancyApp);
            response.setMsgCode(code);
            if (code.equals(ResponseCode.OPERATE_FAILURE)){
                return response;
            };
        }
        return response;
    }

    @ApiOperation(notes = "", value = "删除租户应用关联", tags = { "TenancyApp" })
    @DeleteMapping(value = "/tenancyApp/{tenancyAppId}", produces = "application/json")
    public CommonResponse delete(
            @ApiParam(name = "tenancyAppId", value = "tenancyApp's id(not null,Long)") @PathVariable Long tenancyAppId) {
        CommonResponse response = new CommonResponse();
        if (tenancyAppId == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }
        UapTenancyApp uapTenancyApp = service.get(tenancyAppId);
        if (uapTenancyApp == null) {
            response.setMsgCode(ResponseCode.NOT_EXIST_DATA);
            return response;
        }
        String code = service.delete(uapTenancyApp);
        response.setMsgCode(code);
        return response;
    }

    @ApiOperation(value = "查询租户应用", tags = {"TenancyApp"})
    @GetMapping(value = "/tenancyApp/app/{tenancyId}", produces = "application/json")
    public ListDataResponse<UapApp> findApps(
            @ApiParam(name = "tenancyId", value = "tenancy's id(not null,Long)") @PathVariable Long tenancyId) {
        ListDataResponse<UapApp> response = new ListDataResponse<>();
        if (tenancyId == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }

//        List<UapTenancyApp> res = service.findByTenancyId(tenancyId);
        List<UapApp> uapAppList = service.findAppsByTenancyId(tenancyId);
        response.setData(uapAppList);
        response.setTotal(uapAppList.size());
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }

    @ApiOperation(value = "查询应用租户", tags = {"TenancyApp"})
    @GetMapping(value = "/tenancyApp/tenancy/{appId}", produces = "application/json")
    public ListDataResponse<UapTenancyApp> findTenancids(
            @ApiParam(name = "appId", value = "app's id(not null,Long)") @PathVariable Long appId) {
        ListDataResponse<UapTenancyApp> response = new ListDataResponse<>();
        if (appId == null) {
            response.setMsgCode(ResponseCode.ERR_PARAM);
            return response;
        }

        List<UapTenancyApp> res = service.findByAppId(appId);
        response.setData(res);
        response.setTotal(res.size());
        response.setMsgCode(ResponseCode.OPERATE_SUCCESS);
        return response;
    }
}
