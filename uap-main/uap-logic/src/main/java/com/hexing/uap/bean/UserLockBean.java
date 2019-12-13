package com.hexing.uap.bean;

public class UserLockBean {
	int retryCount = 5;
	Long startTime;
	private String unlockTime;

	public int getRetryCount() {
		return retryCount;
	}

	public Long getStartTime() {
		return startTime;
	}

	public int reduceRetryCount() {
		if (retryCount <= 0) {
			return 0;
		}
		return retryCount--;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getUnlockTime() {
		return unlockTime;
	}

	public void setUnlockTime(String unlockTime) {
		this.unlockTime = unlockTime;
	}

}
