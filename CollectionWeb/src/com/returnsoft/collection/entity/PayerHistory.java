package com.returnsoft.collection.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.returnsoft.collection.converter.DocumentTypeConverter;
import com.returnsoft.collection.enumeration.DocumentTypeEnum;

@Entity 
@Table(name = "payer_history")
public class PayerHistory implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4260059410143867310L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payhis_id")
	private Long idHist;
	
	/*@Column(name = "pay_id")
	private Long id;*/
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "pay_id")
	private Sale sale;
	
	@Column(name = "pay_document_type_id")
	@Convert(converter=DocumentTypeConverter.class)
	private DocumentTypeEnum documentType;
	
	@Column(name = "pay_nuic")
	private Long nuicResponsible;
	
	@Column(name = "pay_lastname_paternal")
	private String lastnamePaternalResponsible;
	
	@Column(name = "pay_lastname_maternal")
	private String lastnameMaternalResponsible;
	
	@Column(name = "pay_firstname")
	private String firstnameResponsible;
	
	@Column(name = "pay_mail")
	private String mail;
	
	@Column(name = "pay_department")
	private String department;
	
	@Column(name = "pay_province")
	private String province;
	
	@Column(name = "pay_district")
	private String district;
	
	@Column(name = "pay_address")
	private String address;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pay_updated_at")
	private Date updatedAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "pay_updated_by")
	private User updatedBy;
	
	/*@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "pay_sal_id")
	private Sale sale;*/

	

	public DocumentTypeEnum getDocumentType() {
		return documentType;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public void setDocumentType(DocumentTypeEnum documentType) {
		this.documentType = documentType;
	}

	public Long getNuicResponsible() {
		return nuicResponsible;
	}

	public void setNuicResponsible(Long nuicResponsible) {
		this.nuicResponsible = nuicResponsible;
	}

	public String getLastnamePaternalResponsible() {
		return lastnamePaternalResponsible;
	}

	public void setLastnamePaternalResponsible(String lastnamePaternalResponsible) {
		this.lastnamePaternalResponsible = lastnamePaternalResponsible;
	}

	public String getLastnameMaternalResponsible() {
		return lastnameMaternalResponsible;
	}

	public void setLastnameMaternalResponsible(String lastnameMaternalResponsible) {
		this.lastnameMaternalResponsible = lastnameMaternalResponsible;
	}

	public String getFirstnameResponsible() {
		return firstnameResponsible;
	}

	public void setFirstnameResponsible(String firstnameResponsible) {
		this.firstnameResponsible = firstnameResponsible;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Long getIdHist() {
		return idHist;
	}

	public void setIdHist(Long idHist) {
		this.idHist = idHist;
	}

	/*public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}*/
	
	
	

}
