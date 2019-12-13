package com.hexing.uap.bpm.app.repository.customer;

import com.hexing.uap.common.bean.PageData;
import com.hexing.uap.common.bean.PagingParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

public class BaseRepository<T> {
	@PersistenceContext
	protected EntityManager entityManager;

	@Value("${spring.datasource.driver-class-name}")
	private String datasource;

	String getLikeParam(String param, StringBuffer paramHql) {
		return "%" + replaceIllegal(param, paramHql) + "%";
	}

	String getStartWithParam(String param, StringBuffer paramHql) {
		return replaceIllegal(param, paramHql) + "%";
	}

	private String replaceIllegal(String param, StringBuffer paramHql) {
		if (StringUtils.isEmpty(param) || (param.indexOf("%") < 0 && param.indexOf("_") < 0)) {
			return param;
		}
		if (datasource.indexOf("mysql") >= 0) {
			return param.replace("%", "\\%").replace("_", "\\_");
		}
		paramHql.append(" escape '\\'");
		return param.replace("%", "\\%").replace("_", "\\_");
	}

	public PageData<T> getPagingData(String countHql, String queryHql, ArrayList<Object> paramValues,
			PagingParam param) {
		Query query = entityManager.createQuery(queryHql);
		Query queryCount = entityManager.createQuery(countHql, Long.class);
		if (paramValues != null) {
			for (int i = 0; i < paramValues.size(); i++) {
				if (paramValues.get(i) != null) {
					query.setParameter(i, paramValues.get(i));
					queryCount.setParameter(i, paramValues.get(i));
				}
			}
		}
		Long totalCount = (Long) queryCount.getSingleResult();
		query.setFirstResult(param.getStart());
		query.setMaxResults(param.getLimit());
		PageData<T> pm = new PageData<T>();
		pm.setData(query.getResultList());
		pm.setTotal(totalCount);
		entityManager.clear();
		return pm;
	}

	public List<T> getList(String queryHql, ArrayList<Object> paramValues) {
		Query query = entityManager.createQuery(queryHql);
		if (paramValues != null) {
			for (int i = 0; i < paramValues.size(); i++) {
				if (paramValues.get(i) != null) {
					query.setParameter(i, paramValues.get(i));
				}
			}
		}
		return query.getResultList();
	}

}
