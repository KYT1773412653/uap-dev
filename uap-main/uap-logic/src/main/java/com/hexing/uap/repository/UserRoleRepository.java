package com.hexing.uap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexing.uap.bean.jpa.UapRole;
import com.hexing.uap.bean.jpa.UapUser;
import com.hexing.uap.bean.jpa.UapUserRole;

@Repository
public interface UserRoleRepository extends JpaRepository<UapUserRole, Long> {
	public void deleteByUapRole(UapRole deleteRole);

	public void deleteByUapUser(UapUser uapUser);

	public List<UapUserRole> findByUapRole(UapRole uapRole);

	public List<UapUserRole> findByUapUser(UapUser uapUser);

	public List<UapUserRole> findByUapUserAndUapRole(UapUser uapUser, UapRole uapRole);

    void deleteByUapUserAndUapRole(UapUser uapUser, UapRole uapRole);
}
