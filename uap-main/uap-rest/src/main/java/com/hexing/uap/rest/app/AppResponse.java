package com.hexing.uap.rest.app;

import com.hexing.uap.bean.jpa.UapApp;

public class AppResponse extends UapApp {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8193979925711779019L;
	
	private String parentName;

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	
}
