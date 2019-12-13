package com.hexing.uap.rest.user.favoritemenu;

import javax.validation.constraints.Size;

import com.hexing.uap.common.bean.PagingParam;
import io.swagger.annotations.ApiModelProperty;

public class UserFavoriteMenuQueryRequest extends PagingParam {
	@Size(max = 16)
	@ApiModelProperty(notes = "类型：1常用0收藏")
	private String type;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
