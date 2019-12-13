package com.hexing.uap.rest.organization;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hexing.uap.bean.jpa.UapMultiTenancy;
import com.hexing.uap.bean.jpa.UapOrganization;
import com.hexing.uap.common.bean.CommonResponse;
import com.hexing.uap.common.bean.ListDataResponse;
import com.hexing.uap.common.bean.ModelResponse;
import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.common.session.SessionUser;
import com.hexing.uap.message.OrgResponseCode;
import com.hexing.uap.repository.param.OrgQuery;
import com.hexing.uap.service.OrganizationService;
import com.hexing.uap.util.ValidatortUtil;
import com.hexing.uap.web.SessionUserUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;

/**
 * 接口功能
 */

@RestController
@Api(tags = { "Org" }, description = "Manage Organization", authorizations = { @Authorization(value = "basicAuth") })
public class OrganizationResource {

	@Autowired
	private OrganizationService service;
	@ApiOperation(notes = "NO_ORG : 查不到业务组织", value = "获取单个业务组织", tags = { "Org" })
	@GetMapping(value = "/org/{orgId}", produces = "application/json")
	public ModelResponse<UapOrganization> get(
			@ApiParam(name = "orgId", value = "orgId's id(not null,String)") @PathVariable Long orgId) {
		ModelResponse<UapOrganization> response = new ModelResponse<>();
		UapOrganization organizationResponse = null;
		if (!StringUtils.isEmpty(orgId)) {
			organizationResponse = service.get(orgId);
			if (organizationResponse == null) {
				response.setMsgCode(OrgResponseCode.NO_ORG);
				response.setData(null);
				return response;
			}
			response.setData(organizationResponse);
			response.setMsgCode(OrgResponseCode.OPERATE_SUCCESS);
		}
		return response;
	}

	@ApiOperation(notes = "NO_PARENT_ORG:父组织不存在，NAME_EXIST:名称已存在", value = "创建单个业务组织", tags = {
			"Org" })
	@PostMapping(value = "/org", produces = "application/json")
	public CommonResponse save(@RequestBody OrganizationCreateRequest param) {
		CommonResponse response = new CommonResponse();
		ValidatortUtil.validate(param);
		UapOrganization organization = new UapOrganization();
		SessionUser tokenUser = SessionUserUtil.getSessionUser();
		Long tenancyId = null;
		if (null != tokenUser.getAdmin()){
			tenancyId = param.getTenancyId();
		}else {
			tenancyId = tokenUser.getTenancyId();
		}
//		appService.checkAppAuthority(SessionUserUtil.getSessionUser(), appId);
		if(tenancyId!=null) {
		    UapMultiTenancy uapMultiTenancy = new UapMultiTenancy();
		    uapMultiTenancy.setId(tenancyId);
		    organization.setUapMultiTenancy(uapMultiTenancy);
		}
		organization.setTimeZone(param.getTimeZone());
		organization.setCode(param.getCode());
		organization.setName(param.getName());
		organization.setParentId(param.getParentId());
		organization.setState(param.getState());
		organization.setType(param.getType());
		String code = service.save(organization);
		response.setMsgCode(code);
		return response;
	}

	@ApiOperation(notes = "NO_ORG:查不到业务组织", value = "更新业务组织信息", tags = { "Org" })
	@PutMapping(value = "/org/{orgId}", produces = "application/json")
	public CommonResponse update(
			@ApiParam(name = "orgId", value = "org's id(not null,Long)") @PathVariable Long orgId,
			@RequestBody OrganizationUpdateRequest param) {
		CommonResponse response = new CommonResponse();
		ValidatortUtil.validate(param);
		
		UapOrganization organization = null;
		if (!StringUtils.isEmpty(orgId)) {
			organization = service.get(orgId);
		}
		if (organization == null) {
			response.setMsgCode(OrgResponseCode.NO_ORG);
			response.setMessage(null);
			return response;
		}
//		appService.checkAppAuthority(SessionUserUtil.getSessionUser(), targetAppIds);
		organization.setTimeZone(param.getTimeZone());
		organization.setCode(param.getCode());
		organization.setName(param.getName());
		organization.setType(param.getType());
		String code = service.save(organization);
		response.setMsgCode(code);
		return response;
	}
	//TODO 下一个版本修改排序方式，改为后端排序
	@ApiOperation(notes = "NO_ORG:查不到业务组织", value = "更新业务组织排序", tags = { "Org" })
	@PutMapping(value = "/org/order", produces = "application/json")
	public CommonResponse updateRank(@RequestBody OrganizationRankRequest orgRankRequest) {
		CommonResponse response = new CommonResponse();
		ValidatortUtil.validate(orgRankRequest);
		
		List<OrganizationRank> orgRankList = orgRankRequest.getOrgRankList();
		Boolean update = true;
		for (OrganizationRank orgRank : orgRankList) {
			Long id = orgRank.getId();
			if (null != id) {
				UapOrganization organization = service.get(id);
				if (organization == null) {
					response.setMsgCode(OrgResponseCode.NO_ORG);
					response.setMessage(null);
					return response;
				}
//				appService.checkAppAuthority(SessionUserUtil.getSessionUser(), organization.getUapApp().getId());
				
				organization.setRankId(orgRank.getRankId());
				String code = service.update(organization);
				if (code.equals(OrgResponseCode.OPERATE_FAILURE)) {
					update = false;
				}
			}
		}
		if (update) {
			response.setMsgCode(OrgResponseCode.OPERATE_SUCCESS);
		} else {
			response.setMsgCode(OrgResponseCode.OPERATE_FAILURE);
		}
		return response;
	}

	@ApiOperation(notes = "NO_ORG:查不到业务组织", value = "更新单个业务组织状态", tags = { "Org" })
	@PutMapping(value = "/org/{orgId}/state", produces = "application/json")
	public CommonResponse updateState(
			@ApiParam(name = "orgId", value = "org's id(not null,String)") @PathVariable String orgId,
			@RequestBody OrganizationStateRequest orgChangeRequest) {
		CommonResponse response = new CommonResponse();
		UapOrganization organization = null;
		if (!StringUtils.isEmpty(orgId)) {
			Long id = Long.parseLong(orgId);
			organization = service.get(id);
		}
		if (organization == null) {
			response.setMsgCode(OrgResponseCode.NO_ORG);
			response.setMessage(null);
			return response;
		}
//		appService.checkAppAuthority(SessionUserUtil.getSessionUser(), organization.getUapApp().getId());
		String state = orgChangeRequest.getState();
		String code = service.updateState(organization,state);
		response.setMsgCode(code);

		return response;
	}

	@ApiOperation(value = "组合条件查询组织数据", tags = { "Org" })
	@PostMapping(value = "/org/list", produces = "application/json")
	public ListDataResponse<UapOrganization> findOrgList(@RequestBody OrgQueryRequest orgQueryRequest,HttpServletRequest httpRequest) {
		ListDataResponse<UapOrganization> response = new ListDataResponse<>();
		ValidatortUtil.validate(orgQueryRequest);

		SessionUser tokenUser = SessionUserUtil.getSessionUser();
		Long tenancyId = null;
		if (BooleanUtils.isTrue(tokenUser.getAdmin())){
			tenancyId = orgQueryRequest.getTenancyId();
		}else {
			if(StringUtils.isEmpty(orgQueryRequest.getParentId())) {
				List<UapOrganization> orgList = new ArrayList<>();
				UapOrganization organization = service.get(tokenUser.getOrgId());
				orgList.add(organization);
				response.setData(orgList);
				response.setTotal(1);
				response.setMsgCode(OrgResponseCode.OPERATE_SUCCESS);
				return response;
			}
			tenancyId = tokenUser.getTenancyId();
		}
		OrgQuery orgQuery = new OrgQuery();
		orgQuery.setName(orgQueryRequest.getName());
		orgQuery.setNo(orgQueryRequest.getNo());
		orgQuery.setState(orgQueryRequest.getState());
		orgQuery.setLimit(orgQueryRequest.getLimit());
		orgQuery.setStart(orgQueryRequest.getStart());
		orgQuery.setParentId(orgQueryRequest.getParentId());
		orgQuery.setTenancyId(tenancyId);
//		if(appId == null || appId.longValue()==0) {
//			appId = RequestUtil.getAppId(httpRequest);
//		}
		orgQuery.setType(orgQueryRequest.getType());
		PageData<UapOrganization> pageData = service.findPageOrgs(orgQuery);
		response.setData(pageData.getData());
		response.setTotal(pageData.getTotal());
		response.setMsgCode(OrgResponseCode.OPERATE_SUCCESS);
		return response;
	}

	@ApiOperation(value = "获取对应业务组织节点的子节点", tags = { "Org" })
	@PostMapping(value = "/org/sublist", produces = "application/json")
	public ListDataResponse<UapOrganization> findSubOrgs(@RequestBody OrgTreeRequest orgTreeRequest,HttpServletRequest httpRequest) {
		ListDataResponse<UapOrganization> response = new ListDataResponse<>();
		SessionUser tokenUser = SessionUserUtil.getSessionUser();
		Long tenancyId = null;
		OrgQuery orgQuery = new OrgQuery();
		orgQuery.setParentId(orgTreeRequest.getParentId());
		if (null != tokenUser.getAdmin()){
			tenancyId = orgTreeRequest.getTenancyId();
			
		}else {
			if(StringUtils.isEmpty(orgTreeRequest.getParentId())) {
				List<UapOrganization> orgList = new ArrayList<>();
				UapOrganization organization = service.get(tokenUser.getOrgId());
				orgList.add(organization);
				response.setData(orgList);
				response.setTotal(1);
				response.setMsgCode(OrgResponseCode.OPERATE_SUCCESS);
				return response;
			}
			
			tenancyId = tokenUser.getTenancyId();
		}
		
		orgQuery.setState(orgTreeRequest.getState());
		Long appId = orgTreeRequest.getAppId();
//		if(appId == null || appId.longValue()==0) {
//			appId = RequestUtil.getAppId(httpRequest);
//		}
		orgQuery.setAppId(appId);
		orgQuery.setTenancyId(tenancyId);
		PageData<UapOrganization> pageData = service.findSubOrgs(orgQuery);
		response.setData(pageData.getData());
		response.setTotal(pageData.getTotal());
		response.setMsgCode(OrgResponseCode.OPERATE_SUCCESS);
		return response;
	}
}
