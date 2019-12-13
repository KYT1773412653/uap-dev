package com.hexing.uap.repository.custom;

import com.hexing.uap.bean.custom.PageModel;
import com.hexing.uap.bean.jpa.UapUser;
import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.repository.param.UserQuery;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2019/5/29 0029.
 */

@Repository
public class RoleUserCustomerRepository extends BaseRepository{

    public PageData<UapUser> findRoleUsers(UserQuery userQuery) {
        PageData<UapUser> res = new PageData<>();
        String orgNo = userQuery.getOrgNo();
        Long roleId = userQuery.getRoleId();
        Long tenantId = userQuery.getTenancyId();

        String sql = " SELECT u.* FROM uap_user u LEFT JOIN uap_organization uo ON u.org_id = uo.id WHERE 1=1" +
         " AND u.tenant_id = "+ tenantId +" AND u.id NOT IN(SELECT uu.user_id FROM uap_user_role uu WHERE uu.role_id = "+ roleId +")";
        if (!StringUtils.isEmpty(orgNo)) {
            sql = sql + " AND uo.no LIKE '" + orgNo + "%'";
        }
        PageModel pageModel = jdbcTemplateSqlPage(jdbcTemplate,sql, userQuery.getStart(), userQuery.getLimit());
        List<Map<String,Object>> data = pageModel.getData();
        List<UapUser> userList = new ArrayList<>();
        System.out.println(data);
        if (null != data && data.size()>=1){
            for (Map<String,Object> map:data){
                UapUser user = new UapUser();
                user.setId(Long.valueOf(String.valueOf(map.get("id"))));
                user.setName(String.valueOf(map.get("name")));
                user.setNo(String.valueOf(map.get("no")));
                userList.add(user);
            }
        }
        res.setTotal(pageModel.getTotalNum());
        res.setData(userList);
        return res;
    }
}
