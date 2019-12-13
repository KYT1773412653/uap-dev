package com.hexing.uap.msg.service.repository.custom;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.beans.BeanWrapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.msg.model.jpa.UapMsgInfo;
import com.hexing.uap.msg.model.request.MsgReq;

@Repository("msgCustomRepository")
public class MsgCustomRepository extends BaseRepository<UapMsgInfo> {

	public PageData<UapMsgInfo> getMsgUserList(MsgReq msgReq,Long userId){
		
		StringBuffer queryHql = new StringBuffer();
		queryHql.append(" select a.id,a.msg_title,a.msg_content,a.msg_code,a.sender_nm,a.msg_level,c.state,a.create_time from uap_msg_info a ");
		queryHql.append(" join uap_msg_receiver c on a.id = c.msg_id and c.user_id=");
		queryHql.append(userId);
		queryHql.append("  where a.state = '1' ");
		
		String state = msgReq.getState();
		String msgLevel = msgReq.getMsgLevel();
		int pageNumber = msgReq.getPageNumber();
		int size = msgReq.getPageSize();

		if (!StringUtils.isEmpty(msgLevel)) {
			queryHql.append(" and a.msg_level='");
			queryHql.append(msgLevel);
			queryHql.append("'");
		}
		
		if (!StringUtils.isEmpty(state)) {
			queryHql.append(" and c.state='");
			queryHql.append(state);
			queryHql.append("'");
		}
		if (!StringUtils.isEmpty(msgReq.getCodeOrName())) {
			queryHql.append(" and (a.msg_title like '%");
			queryHql.append(msgReq.getCodeOrName());
			queryHql.append("%' or a.msg_content like '%");
			queryHql.append(msgReq.getCodeOrName());
			queryHql.append("%' or a.msg_code like '%");
			queryHql.append(msgReq.getCodeOrName());
			queryHql.append("%') ");
		}
		
		if(msgReq.getStartTime()!=null) {
			queryHql.append(" and a.create_time>=");
			queryHql.append(msgReq.getStartTime());
		}
		if(msgReq.getEndTime()!=null) {
			queryHql.append(" and a.create_time<=");
			queryHql.append(msgReq.getEndTime());
		}
		queryHql.append(" order by a.create_time desc ");
		 
		return jdbcTemplateSqlPageVo(queryHql.toString(), (pageNumber-1)*size, size); 
	}
	

	public PageData<UapMsgInfo> jdbcTemplateSqlPageVo(@NotNull String sql, Integer startIndex, Integer pageSize) {
		PageData<UapMsgInfo> pageModel = new PageData<>();
		try {
			//个数查询
			String countSql = "select count(1) totalCount from (" + sql
					+ ")  tmp";
			SqlRowSet countRs = jdbcTemplate.queryForRowSet(countSql);
			countRs.next();
			pageModel.setTotal(countRs.getInt("totalCount"));

			//数据查询
			String pageSql = sql;
			if (databaseProductName.toLowerCase().contains("mysql")) {
				pageSql= "select *,0 as rn from ( "+sql+ " ) as tmp limit "+startIndex+","+pageSize;
			} else if (databaseProductName.toLowerCase().contains("oracle")) {
                Integer endIndex = startIndex + pageSize + 1;
                Integer startNew = startIndex + 1;
				pageSql = "select row_s.* from ( select row_.*, rownum rn  from (" + sql + " )  row_ where rownum <  " + endIndex + ")  row_s where row_s.rn >=" + startNew;
			}
			List<UapMsgInfo> dataRs = jdbcTemplate.query(pageSql, new BeanPropertyRowMapper<UapMsgInfo>(UapMsgInfo.class){    
		        @Override    
		        protected void initBeanWrapper(BeanWrapper bw) {    
		            super.initBeanWrapper(bw);    
		        }    
		    });
			pageModel.setData(dataRs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pageModel;
	}

	public PageData<UapMsgInfo> getMsgList(MsgReq msgReq,Long tenancyId){
		StringBuffer queryHql = new StringBuffer();
		queryHql.append(" select a.id,a.msg_title ,a.msg_content,a.msg_code,a.sender_nm,a.msg_level,a.create_time from uap_msg_info a ");
		String msgLevel = msgReq.getMsgLevel();
		int pageNumber = msgReq.getPageNumber();
		int size = msgReq.getPageSize();
		queryHql.append("  where a.state = '1' ");
		if (!StringUtils.isEmpty(msgLevel)) {
			queryHql.append(" and a.msg_level='");
			queryHql.append(msgLevel);
			queryHql.append("'");
		}
		if (!StringUtils.isEmpty(msgReq.getCodeOrName())) {
			queryHql.append(" and (a.msg_title like '%");
			queryHql.append(msgReq.getCodeOrName());
			queryHql.append("%' or a.msg_content like '%");
			queryHql.append(msgReq.getCodeOrName());
			queryHql.append("%' or a.msg_code like '%");
			queryHql.append(msgReq.getCodeOrName());
			queryHql.append("%') ");
		}
		
		if(msgReq.getStartTime()!=null) {
			queryHql.append(" and a.create_time>=");
			queryHql.append(msgReq.getStartTime());
		}
		if(msgReq.getEndTime()!=null) {
			queryHql.append(" and a.create_time<=");
			queryHql.append(msgReq.getEndTime());
		}
		if(tenancyId!=null) {
			queryHql.append(" and (a.tenancy_id is null or a.tenancy_id =");
			queryHql.append(tenancyId);
			queryHql.append(")");
		}
		queryHql.append(" order by a.create_time desc ");
		return jdbcTemplateSqlPageVo(queryHql.toString(), (pageNumber-1)*size, size);
		 
	}
}
