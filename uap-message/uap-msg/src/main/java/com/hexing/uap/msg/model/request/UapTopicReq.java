package com.hexing.uap.msg.model.request;

import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author HD2042
 *
 */
public class UapTopicReq {

	private String codeOrName;

	private String level;

	private String sendModel;

	private Long appId;

	private String state;

	private Long startTime;

	private Long endTime;

	private int pageNumber;

	private int pageSize;

	@Min(value = 0)
	@ApiModelProperty(notes = "分页，页数")
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Min(value = 0)
	@ApiModelProperty(notes = "查询总条数,最小10")
	public int getPageSize() {
		if (pageSize <= 10) {
			pageSize = 10;
		}

		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	@ApiModelProperty(required = false, notes = "message level")
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	@ApiModelProperty(required = false, notes = "message send model")
	public String getSendModel() {
		return sendModel;
	}

	public void setSendModel(String sendModel) {
		this.sendModel = sendModel;
	}

	@ApiModelProperty(required = false, notes = "message topic state")
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@ApiModelProperty(required = true, notes = "message topic app")
	public Long getAppId() {
		return appId;
	}

	public void setAppId(Long appId) {
		this.appId = appId;
	}

	@ApiModelProperty(notes = "message topic start time")
	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	@ApiModelProperty(notes = "message topic end time")
	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	@ApiModelProperty(notes = "message topic code or name")
	public String getCodeOrName() {
		return codeOrName;
	}

	public void setCodeOrName(String codeOrName) {
		this.codeOrName = codeOrName;
	}

}
