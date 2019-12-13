package com.hexing.uap.rest.menu;

import javax.validation.constraints.Size;

/**
 * 更新组织排序字段
 */

public class MenuRank {

    @Size(max = 64)
    private String id;
    private Integer rankId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getRankId() {
        return rankId;
    }

    public void setRankId(Integer rankId) {
        this.rankId = rankId;
    }
}
