package com.hexing.uap.rest.menu.display;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * Created by Administrator on 2018/12/25 0025.
 */
public class MneuDisplayMenuRequest {

    @NotNull()
    @Size(max = 64, min = 1)
    @ApiModelProperty(notes = "菜单id,长度不超过64")
    private String menuId;

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }
}
