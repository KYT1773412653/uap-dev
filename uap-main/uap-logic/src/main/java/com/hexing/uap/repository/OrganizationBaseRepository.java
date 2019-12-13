package com.hexing.uap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexing.uap.bean.jpa.UapOrganizationBase;

@Repository
/**
 * 
 * @author ZCP
 *
 */
public interface OrganizationBaseRepository extends JpaRepository<UapOrganizationBase, Long> {

	public List<UapOrganizationBase> findByParentId(Long parentId);
}
