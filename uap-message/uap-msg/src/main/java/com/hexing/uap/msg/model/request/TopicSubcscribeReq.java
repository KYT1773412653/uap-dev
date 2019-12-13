package com.hexing.uap.msg.model.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author HD2042
 *
 */
public class TopicSubcscribeReq {
	private String topicId;
	private int pageNumber;
	private int pageSize;

	@ApiModelProperty(notes = "主题id")
	public String getTopicId() {
		return topicId;
	}

	public void setTopicId(String topicId) {
		this.topicId = topicId;
	}

	@Min(value = 0)
	@ApiModelProperty(notes = "分页，页数")
	public int getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Min(value = 0)
	@Max(value = 30)
	@ApiModelProperty(notes = "查询总条数,最大值30")
	public int getPageSize() {
		if (pageSize <= 0 || pageSize > 30) {
			pageSize = 30;
		}

		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

}
