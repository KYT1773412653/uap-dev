package com.hexing.uap.common.bean;

import java.util.List;

public class PageData<T> {

	List<T> data;
	long total;

	public List<T> getData() {
		return data;
	}

	public PageData<T> setData(List<T> data) {
		this.data = data;
		return this;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

}
