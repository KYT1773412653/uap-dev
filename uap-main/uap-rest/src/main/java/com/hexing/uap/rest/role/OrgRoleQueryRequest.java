package com.hexing.uap.rest.role;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

public class OrgRoleQueryRequest {

	private Long parentId;
	private Long orgId;
	private Long appId;
	private String name;
	private String state;
	private Long tenancyId;
	private int start;
	private int limit;

	@ApiModelProperty(required = true, notes = "所属租户,长度不超过20")
	public Long getTenancyId() {
		return tenancyId;
	}

	public void setTenancyId(Long tenancyId) {
		this.tenancyId = tenancyId;
	}

	@ApiModelProperty(required = true, notes = "所属应用,长度不超过20")
	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}
	
	@Size(max = 128)
	@ApiModelProperty(notes = "根据角色名模糊查询,长度不超过128")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@ApiModelProperty(notes = "根据父角色Id查询,长度不超过20")
	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	@ApiModelProperty(notes = "根据组织ID精确查询,长度不超过20")
	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	
	@Size(max = 16)
	@ApiModelProperty(notes = "根据状态精确查询,长度不超过16")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	@Min(value = 0)
	@ApiModelProperty(notes = "查询起始条目")
	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	@Min(value = 0)
	@Max(value = 30)
	@ApiModelProperty(notes = "查询总条数,最大值30")
	public int getLimit() {
		if (limit <= 0 || limit > 30) {
			limit = 30;
		}

		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
