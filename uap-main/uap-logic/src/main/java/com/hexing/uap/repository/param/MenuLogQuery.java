package com.hexing.uap.repository.param;

import com.hexing.uap.common.bean.PagingParam;

/** 
 * Des:
 * @author hua.zhiwei<br>
 * @CreateDate 2019年5月31日
 */

public class MenuLogQuery extends PagingParam {
	private String userNo;
	private Long beginTime;
	private Long endTime;
	private String menuId;
	private String menuDisplayName;
	private Long tenancyId;
	public String getUserNo() {
		return userNo;
	}
	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
	public Long getBeginTime() {
		return beginTime;
	}
	public void setBeginTime(Long beginTime) {
		this.beginTime = beginTime;
	}
	public Long getEndTime() {
		return endTime;
	}
	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}
	public String getMenuId() {
		return menuId;
	}
	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}
	public String getMenuDisplayName() {
		return menuDisplayName;
	}
	public void setMenuDisplayName(String menuDisplayName) {
		this.menuDisplayName = menuDisplayName;
	}
	public Long getTenancyId() {
		return tenancyId;
	}
	public void setTenancyId(Long tenancyId) {
		this.tenancyId = tenancyId;
	}
}
