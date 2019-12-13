package com.hexing.uap.rest;

import java.util.List;

public class ListDataResponse<T> extends CommonResponse {

	List<T> data;
	long total;

	public List<T> getData() {
		return data;
	}

	public ListDataResponse<T> setData(List<T> data) {
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
