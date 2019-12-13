package com.hexing.uap.bean.jpa;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * UapRole entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "UAP_ROLE")
public class UapRole implements java.io.Serializable {

	// Fields

	private static final long serialVersionUID = 4893017079422267330L;
	private String code;
	private Long id;
	private Long insertTime;
	private String isLeaf;
	private String name;
	private Long orgId;
	private Long parentId;
	private String state;
	private String type;
	private String typeExt;
	@JsonIgnore
	private UapApp uapApp;
	@JsonIgnore
	private UapMultiTenancy uapMultiTenancy;
	// Constructors
	@JsonIgnore
	private Set<UapRoleMenu> uapRoleMenus = new HashSet<UapRoleMenu>(0);
	@JsonIgnore
	private Set<UapUserRole> uapUserRoles = new HashSet<UapUserRole>(0);
	private Long updateTime;

	/** default constructor */
	public UapRole() {
	}

	/** minimal constructor */
	public UapRole(Long id, String code, Long insertTime, String isLeaf, String name, String state, Long updateTime,
			String type, String typeExt) {
		this.id = id;
		this.code = code;
		this.insertTime = insertTime;
		this.isLeaf = isLeaf;
		this.name = name;
		this.state = state;
		this.type = type;
		this.typeExt = typeExt;
		this.updateTime = updateTime;
	}

	/** full constructor */
	public UapRole(Long id, UapApp uapApp, String code, Long insertTime, String isLeaf, String name, Long orgId,
			String type, Long parentId, String state, String typeExt, Long updateTime, Set<UapUserRole> uapUserRoles,
			Set<UapRoleMenu> uapRoleMenus) {
		this.id = id;
		this.uapApp = uapApp;
		this.code = code;
		this.insertTime = insertTime;
		this.isLeaf = isLeaf;
		this.name = name;
		this.orgId = orgId;
		this.type = type;
		this.typeExt = typeExt;
		this.parentId = parentId;
		this.state = state;
		this.updateTime = updateTime;
		this.uapUserRoles = uapUserRoles;
		this.uapRoleMenus = uapRoleMenus;
	}

	@Column(name = "CODE", nullable = false, length = 48)

	public String getCode() {
		return this.code;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "roleId")
	@TableGenerator(table = "UAP_SEQUENCE", name = "roleId", pkColumnValue = "roleId", allocationSize = 1)
	@Column(name = "ID", unique = true, nullable = false)

	public Long getId() {
		return this.id;
	}

	@Column(name = "INSERT_TIME")

	public Long getInsertTime() {
		return this.insertTime;
	}

	@Column(name = "IS_LEAF", length = 16)

	public String getIsLeaf() {
		return this.isLeaf;
	}

	@Column(name = "NAME", nullable = false, length = 128)

	public String getName() {
		return this.name;
	}

	@Column(name = "ORG_ID")

	public Long getOrgId() {
		return orgId;
	}

	@Column(name = "PARENT_ID")

	public Long getParentId() {
		return this.parentId;
	}

	@Column(name = "STATE", length = 16)

	public String getState() {
		return this.state;
	}

	@Column(name = "TYPE", length = 16)

	public String getType() {
		return type;
	}

	@Column(name = "TYPE_EXT")
	public String getTypeExt() {
		return typeExt;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "APP_ID", nullable = false)

	public UapApp getUapApp() {
		return this.uapApp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TENANT_ID", nullable = true)
	public UapMultiTenancy getUapMultiTenancy() {
		return uapMultiTenancy;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "uapRole")

	public Set<UapRoleMenu> getUapRoleMenus() {
		return this.uapRoleMenus;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "uapRole")

	public Set<UapUserRole> getUapUserRoles() {
		return this.uapUserRoles;
	}

	@Column(name = "UPDATE_TIME")

	public Long getUpdateTime() {
		return this.updateTime;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setInsertTime(Long insertTime) {
		this.insertTime = insertTime;
	}

	public void setIsLeaf(String isLeaf) {
		this.isLeaf = isLeaf;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setTypeExt(String typeExt) {
		this.typeExt = typeExt;
	}

	public void setUapApp(UapApp uapApp) {
		this.uapApp = uapApp;
	}

	public void setUapMultiTenancy(UapMultiTenancy uapMultiTenancy) {
		this.uapMultiTenancy = uapMultiTenancy;
	}

	public void setUapRoleMenus(Set<UapRoleMenu> uapRoleMenus) {
		this.uapRoleMenus = uapRoleMenus;
	}

	public void setUapUserRoles(Set<UapUserRole> uapUserRoles) {
		this.uapUserRoles = uapUserRoles;
	}

	public void setUpdateTime(Long updateTime) {
		this.updateTime = updateTime;
	}

}