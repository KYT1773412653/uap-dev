package com.hexing.uap.msg.model.request;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author HD2042
 *
 */
public class UapTopicStateReq {

	private String code;

	private int state;

	@ApiModelProperty(required = true, notes = "message code")
	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	@ApiModelProperty(required = true, notes = "message topic state")
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}
