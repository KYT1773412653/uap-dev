package com.hexing.uap.message;

/**
 * <p>
 * 平台配置项名称定义
 * 
 * @author hua.zhiwei<br>
 */

public interface PropertyDef {

	/** 密码有效最小天数 */
	String PASSWORD_EFF_MIN = "password_eff_min";
	/** 密码有效最天天数 */
	String PASSWORD_EFF_MAX = "password_eff_max";
	/** 密码最近不可重复次数 */
	String PASSWORD_CHECK_TIMES = "password_check_times";
	/** 密码有效天数 */
	String PASSWORD_EXPIRY_TIME = "pwd_expiry_time";
	/** 密码提前提醒天数 */
	String PASSWORD_NOTIFY_DAYS = "password_notify";
}
