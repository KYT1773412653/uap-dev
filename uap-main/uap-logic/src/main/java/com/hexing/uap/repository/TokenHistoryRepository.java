package com.hexing.uap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexing.uap.bean.jpa.UapTokenHistory;
@Repository
public interface TokenHistoryRepository extends JpaRepository<UapTokenHistory, Long> {
	public List<UapTokenHistory> findByOwnerIdAndState(Long ownerId, String state);

	public List<UapTokenHistory> findByRefreshTokenAndState(String refreshToken, String state);

	public List<UapTokenHistory> findByState(String state);

	public List<UapTokenHistory> findByToken(String token);
	public List<UapTokenHistory> findByOwnerIdAndType(Long ownerId, String type);
}
