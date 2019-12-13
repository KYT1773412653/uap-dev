package com.hexing.uap.bean.jpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

/**
 * UapUserRole entity. @author MyEclipse Persistence Tools
 */
@Entity
@Table(name = "UAP_USER_ROLE")
public class UapUserRole implements java.io.Serializable {

	private static final long serialVersionUID = -2076419197590123777L;

	// Fields

	private Long id;

	private UapRole uapRole;
	private UapUser uapUser;

	// Constructors

	/** default constructor */
	public UapUserRole() {
	}

	/** full constructor */
	public UapUserRole(Long id, UapUser uapUser, UapRole uapRole) {
		this.id = id;
		this.uapUser = uapUser;
		this.uapRole = uapRole;
	}

	// Property accessors
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "userRoleId")
	@TableGenerator(table = "UAP_SEQUENCE", name = "userRoleId", pkColumnValue = "userRoleId", allocationSize = 1)
	@Column(name = "ID", unique = true, nullable = false)

	public Long getId() {
		return this.id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ROLE_ID", nullable = false)

	public UapRole getUapRole() {
		return this.uapRole;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USER_ID", nullable = false)

	public UapUser getUapUser() {
		return this.uapUser;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setUapRole(UapRole uapRole) {
		this.uapRole = uapRole;
	}

	public void setUapUser(UapUser uapUser) {
		this.uapUser = uapUser;
	}

}