package com.hexing.uap.common.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/5/29 0029.
 */
public class PageModel {

    private List<Map<String,Object>> data;
    private Integer totalNum;

    public List<Map<String, Object>> getData() {
        return data;
    }

    public void setData(List<Map<String, Object>> data) {
        this.data = data;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }
}
