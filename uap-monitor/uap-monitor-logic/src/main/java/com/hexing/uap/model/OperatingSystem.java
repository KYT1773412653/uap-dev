package com.hexing.uap.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/** 
 * Des:
 * @author hua.zhiwei<br>
 * @CreateDate 2019年8月28日
 */

public class OperatingSystem implements Serializable {

	/**
	 * serialVersionUID <br>
	 */
	private static final long serialVersionUID = 1L;
	private String family;
	private String manufacturer;
	private int processId;
	private int processCount;
	private int threadCount;
	private NetworkParam networkParam;
	private List<FileSystem> fileSystems = new ArrayList<>();
	private String version;
	private String codeName;
	private String buildNumber;
	private List<OSProcess> osProcessList;
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {
		this.family = family;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public int getProcessId() {
		return processId;
	}
	public void setProcessId(int processId) {
		this.processId = processId;
	}
	public int getProcessCount() {
		return processCount;
	}
	public void setProcessCount(int processCount) {
		this.processCount = processCount;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public NetworkParam getNetworkParam() {
		return networkParam;
	}
	public void setNetworkParam(NetworkParam networkParam) {
		this.networkParam = networkParam;
	}
	public List<FileSystem> getFileSystems() {
		return fileSystems;
	}
	public void setFileSystems(List<FileSystem> fileSystems) {
		this.fileSystems = fileSystems;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getCodeName() {
		return codeName;
	}
	public void setCodeName(String codeName) {
		this.codeName = codeName;
	}
	public String getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}
	public List<OSProcess> getOsProcessList() {
		return osProcessList;
	}
	public void setOsProcessList(List<OSProcess> osProcessList) {
		this.osProcessList = osProcessList;
	}
}
