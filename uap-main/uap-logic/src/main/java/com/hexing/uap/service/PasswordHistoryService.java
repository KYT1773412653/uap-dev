package com.hexing.uap.service;

import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.hexing.uap.bean.jpa.UapPasswordHistory;
import com.hexing.uap.bean.jpa.UapProperty;
import com.hexing.uap.bean.jpa.UapUser;
import com.hexing.uap.common.UapConstant;
import com.hexing.uap.common.message.ResponseCode;
import com.hexing.uap.message.PropertyDef;
import com.hexing.uap.repository.PasswordHistoryRepository;
import com.hexing.uap.repository.PropertyRepository;
import com.hexing.uap.util.DateTimeUtil;

/**
 * <p>
 * 用户密码历史服务
 * 
 * @author hua.zhiwei<br>
 */
@Service
public class PasswordHistoryService {

	@Autowired
	private PasswordHistoryRepository passwordHistoryRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private UserService userService;

	/**
	 * <p>
	 * 保存密码历史
	 * 
	 * @author hua.zhiwei<br>
	 * @param userNo
	 *            用户账号
	 * @param password
	 *            旧密码，经过BCryptPasswordEncoder加密的密码
	 * @return 操作结果<br>
	 */
	public String save(UapPasswordHistory history) {
		history.setInTime(DateTimeUtil.clock.millis());
		passwordHistoryRepository.save(history);
		return ResponseCode.OPERATE_SUCCESS;

	}

	/**
	 * <p>
	 * 检查密码是否与前几次的相同，如果不相同返回true，反之返回false
	 * 
	 * @author hua.zhiwei<br>
	 * @param userNo
	 *            用户账号
	 * @param password
	 *            未经过BCryptPasswordEncoder加密的密码
	 * @return 检查结果<br>
	 */
	public boolean check(String userNo, String password) {
		UapUser user = userService.findByNo(userNo);
		Long tenantId = user.getUapMultiTenancy().getId();
		List<UapProperty> list = propertyRepository.findByItemNameAndTenantId(PropertyDef.PASSWORD_CHECK_TIMES,
				tenantId);
		if (CollectionUtils.isEmpty(list) || StringUtils.isEmpty(list.get(0).getItemValue())) {
			return true;
		}
		int count = Integer.valueOf(list.get(0).getItemValue());
		if (count <= 0) {
			return true;
		}
		List<UapPasswordHistory> history = passwordHistoryRepository.findByUserNo(userNo);
		if (CollectionUtils.isEmpty(history)) {
			return true;
		}
		return history.stream()
				.filter(passwordHistory -> UapConstant.UPDATE_PASSWORD_SELF.equals(passwordHistory.getType()))
				.sorted(Comparator.comparingLong(UapPasswordHistory::getInTime).reversed()).limit(count)
				.noneMatch(passwordHistory -> userService.passwordMatch(password, passwordHistory.getPassword()));

	}

	public List<UapPasswordHistory> findByUserNo(String userNo) {
		return passwordHistoryRepository.findByUserNo(userNo);
	}
}
