package com.hexing.uap.rest.menu.display;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Created by Administrator on 2019/5/21 0021.
 */
public class MneuDisplayAppDeleteRequest {

    @NotNull
    @ApiModelProperty(notes = "应用id")
    private String appId;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
