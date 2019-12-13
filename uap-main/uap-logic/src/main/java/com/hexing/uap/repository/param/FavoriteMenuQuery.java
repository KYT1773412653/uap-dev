package com.hexing.uap.repository.param;

import com.hexing.uap.common.bean.PagingParam;

/**
 * 菜单复杂检索参数
 */
public class FavoriteMenuQuery extends PagingParam {

	private Long userId;
	private String type;
	private String menuId;
	
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

}
