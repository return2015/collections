package com.returnsoft.collection.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.returnsoft.collection.converter.UserTypeConverter;
import com.returnsoft.collection.enumeration.UserTypeEnum;


@Entity
@Table(name = "user")
public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usr_id")
	private Integer id;

	

	@Column(name = "usr_firstname")
	private String firstname;

	@Column(name = "usr_lastname")
	private String lastname;

	@Column(name = "usr_document_number")
	private String documentNumber;

	@Column(name = "usr_username")
	private String username;

	@Column(name = "usr_password")
	private String password;
	
	@Column(name = "usr_password_supervisor")
	private String passwordSupervisor;
	
	@Column(name = "usr_is_active")
	private Boolean isActive;

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "usr_id_parent")
	private User coordinator;

	@Column(name = "usr_type_id")
	@Convert(converter = UserTypeConverter.class)
	private UserTypeEnum userType;


	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "user_bank", joinColumns = { @JoinColumn(name = "usrban_usr_id") }, inverseJoinColumns = { @JoinColumn(name = "usrban_ban_id") })
	private List<Bank> banks;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getDocumentNumber() {
		return documentNumber;
	}

	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public User getCoordinator() {
		return coordinator;
	}

	public void setCoordinator(User coordinator) {
		this.coordinator = coordinator;
	}

	

	public UserTypeEnum getUserType() {
		return userType;
	}

	public void setUserType(UserTypeEnum userType) {
		this.userType = userType;
	}

	public List<Bank> getBanks() {
		return banks;
	}

	public void setBanks(List<Bank> banks) {
		this.banks = banks;
	}

	public String getPasswordSupervisor() {
		return passwordSupervisor;
	}

	public void setPasswordSupervisor(String passwordSupervisor) {
		this.passwordSupervisor = passwordSupervisor;
	}

	
	

	

	
	
	

}
