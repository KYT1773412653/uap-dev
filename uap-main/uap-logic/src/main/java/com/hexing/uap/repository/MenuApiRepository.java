package com.hexing.uap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexing.uap.bean.jpa.UapMenu;
import com.hexing.uap.bean.jpa.UapMenuApi;
import com.hexing.uap.bean.jpa.UapRestApi;

@Repository
public interface MenuApiRepository extends JpaRepository<UapMenuApi, String> {
	public void deleteByUapMenu(UapMenu menu);

	public void deleteByUapRestApi(UapRestApi restApi);

	public List<UapMenuApi> findByUapMenu(UapMenu menu);

	public List<UapMenuApi> findByUapMenuAndUapRestApi(UapMenu menu, UapRestApi api);

	public List<UapMenuApi> findByUapRestApi(UapRestApi restApi);
}
