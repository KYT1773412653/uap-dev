package com.hexing.uap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexing.uap.bean.jpa.UapMenu;
import com.hexing.uap.bean.jpa.UapRole;
import com.hexing.uap.bean.jpa.UapRoleMenu;

@Repository
public interface RoleMenuRepository extends JpaRepository<UapRoleMenu, Long> {
	public void deleteByUapMenu(UapMenu menu);

	public void deleteByUapRole(UapRole uapRole);

	public List<UapRoleMenu> findByUapMenu(UapMenu uapMenu);

	public List<UapRoleMenu> findByUapRole(UapRole uapRole);

    void deleteByUapMenuAndUapRole(UapMenu uapMenu, UapRole roleChild);
}
