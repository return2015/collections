package com.returnsoft.collection.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity 
@Table(name = "sale")
public class Sale implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sal_id")
	private Long id;
	
	@Column(name = "sal_code")
	private String code;
	
	@Column(name = "sal_document_type")
	private String documentType;
	
	@Column(name = "sal_account_number")
	private Long accountNumber;
	
	@Column(name = "sal_nuic_contractor")
	private Integer nuicContractor;
	
	@Column(name = "sal_lastname_paternal_contractor")
	private String lastnamePaternalContractor;
	
	@Column(name = "sal_lastname_maternal_contractor")
	private String lastnameMaternalContractor;
	
	@Column(name = "sal_firstname_contractor")
	private String firstnameContractor;
	
	@Column(name = "sal_nuic_insured")
	private Integer nuicInsured;
	
	@Column(name = "sal_lastname_paternal_insured")
	private String lastnamePaternalInsured;
	
	@Column(name = "sal_lastname_maternal_insured")
	private String lastnameMaternalInsured;
	
	@Column(name = "sal_firstname_insured")
	private String firstnameInsured;
	
	@Column(name = "sal_phone1")
	private Integer phone1;
	
	@Column(name = "sal_phone2")
	private Integer phone2;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sal_date")
	private Date dateOfSale;
	
	@Column(name = "sal_channel")
	private String channelOfSale;
	
	@Column(name = "sal_place")
	private String placeOfSale;
	
	@Column(name = "sal_vendor_code")
	private String vendorCode;
	
	@Column(name = "sal_vendor_name")
	private String vendorName;
	
	@Column(name = "sal_policy_number")
	private String policyNumber;
	
	@Column(name = "sal_certificate_number")
	private String certificateNumber;
	
	@Column(name = "sal_proposal_number")
	private String proposalNumber;
	
	@Column(name = "sal_product_description")
	private String productDescription;
	
	@Column(name = "sal_collection_period")
	private String collectionPeriod;
	
	@Column(name = "sal_collection_type")
	private String collectionType;
	
	@Column(name = "sal_insurance_premium")
	private BigDecimal insurancePremium;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sal_audit_date")
	private Date auditDate;
	
	@Column(name = "sal_audit_user")
	private String auditUser;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sal_created_at")
	private Date createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sal_updated_at")
	private Date updatedAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_created_by")
	private User createdBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_updated_by")
	private User updatedBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_com_id")
	private Commerce commerce;
	
	@Column(name = "sal_affiliation")
	private Boolean affiliation;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sal_affiliation_date")
	private Date affiliationDate;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_affiliation_usr_id")
	private User affiliationUser;
	
	@Column(name = "sal_physical_notifications")
	private Short physicalNotifications;
	
	@Column(name = "sal_virtual_notifications")
	private Short virtualNotifications;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_not_id")
	private Notification notification;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_crecar_id")
	private CreditCard creditCard;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_salsta_id")
	private SaleState saleState;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_pay_id")
	private Payer payer;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}

	public String getFirstnameContractor() {
		return firstnameContractor;
	}

	public void setFirstnameContractor(String firstnameContractor) {
		this.firstnameContractor = firstnameContractor;
	}

	public String getFirstnameInsured() {
		return firstnameInsured;
	}

	public void setFirstnameInsured(String firstnameInsured) {
		this.firstnameInsured = firstnameInsured;
	}

	public Long getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(Long accountNumber) {
		this.accountNumber = accountNumber;
	}

	public Integer getNuicContractor() {
		return nuicContractor;
	}

	public void setNuicContractor(Integer nuicContractor) {
		this.nuicContractor = nuicContractor;
	}

	public String getLastnamePaternalContractor() {
		return lastnamePaternalContractor;
	}

	public void setLastnamePaternalContractor(String lastnamePaternalContractor) {
		this.lastnamePaternalContractor = lastnamePaternalContractor;
	}

	public String getLastnameMaternalContractor() {
		return lastnameMaternalContractor;
	}

	public void setLastnameMaternalContractor(String lastnameMaternalContractor) {
		this.lastnameMaternalContractor = lastnameMaternalContractor;
	}

	public Integer getNuicInsured() {
		return nuicInsured;
	}

	public void setNuicInsured(Integer nuicInsured) {
		this.nuicInsured = nuicInsured;
	}

	public String getLastnamePaternalInsured() {
		return lastnamePaternalInsured;
	}

	public void setLastnamePaternalInsured(String lastnamePaternalInsured) {
		this.lastnamePaternalInsured = lastnamePaternalInsured;
	}

	public String getLastnameMaternalInsured() {
		return lastnameMaternalInsured;
	}

	public void setLastnameMaternalInsured(String lastnameMaternalInsured) {
		this.lastnameMaternalInsured = lastnameMaternalInsured;
	}

	public Integer getPhone1() {
		return phone1;
	}

	public void setPhone1(Integer phone1) {
		this.phone1 = phone1;
	}

	public Integer getPhone2() {
		return phone2;
	}

	public void setPhone2(Integer phone2) {
		this.phone2 = phone2;
	}

	public Date getDateOfSale() {
		return dateOfSale;
	}

	public void setDateOfSale(Date dateOfSale) {
		this.dateOfSale = dateOfSale;
	}

	public String getChannelOfSale() {
		return channelOfSale;
	}

	public void setChannelOfSale(String channelOfSale) {
		this.channelOfSale = channelOfSale;
	}

	public String getPlaceOfSale() {
		return placeOfSale;
	}

	public void setPlaceOfSale(String placeOfSale) {
		this.placeOfSale = placeOfSale;
	}

	public String getVendorCode() {
		return vendorCode;
	}

	public void setVendorCode(String vendorCode) {
		this.vendorCode = vendorCode;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getCertificateNumber() {
		return certificateNumber;
	}

	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}

	public String getProposalNumber() {
		return proposalNumber;
	}

	public void setProposalNumber(String proposalNumber) {
		this.proposalNumber = proposalNumber;
	}

	public String getProductDescription() {
		return productDescription;
	}

	public void setProductDescription(String productDescription) {
		this.productDescription = productDescription;
	}

	public String getCollectionPeriod() {
		return collectionPeriod;
	}

	public void setCollectionPeriod(String collectionPeriod) {
		this.collectionPeriod = collectionPeriod;
	}

	public String getCollectionType() {
		return collectionType;
	}

	public void setCollectionType(String collectionType) {
		this.collectionType = collectionType;
	}

	public BigDecimal getInsurancePremium() {
		return insurancePremium;
	}

	public void setInsurancePremium(BigDecimal insurancePremium) {
		this.insurancePremium = insurancePremium;
	}

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public String getAuditUser() {
		return auditUser;
	}

	public void setAuditUser(String auditUser) {
		this.auditUser = auditUser;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getAffiliationDate() {
		return affiliationDate;
	}

	public void setAffiliationDate(Date affiliationDate) {
		this.affiliationDate = affiliationDate;
	}

	public User getAffiliationUser() {
		return affiliationUser;
	}

	public void setAffiliationUser(User affiliationUser) {
		this.affiliationUser = affiliationUser;
	}

	public Boolean getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(Boolean affiliation) {
		this.affiliation = affiliation;
	}

	public SaleState getSaleState() {
		return saleState;
	}

	public void setSaleState(SaleState saleState) {
		this.saleState = saleState;
	}

	public Commerce getCommerce() {
		return commerce;
	}

	public void setCommerce(Commerce commerce) {
		this.commerce = commerce;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Payer getPayer() {
		return payer;
	}

	public void setPayer(Payer payer) {
		this.payer = payer;
	}

	public Short getPhysicalNotifications() {
		return physicalNotifications;
	}

	public void setPhysicalNotifications(Short physicalNotifications) {
		this.physicalNotifications = physicalNotifications;
	}

	public Short getVirtualNotifications() {
		return virtualNotifications;
	}

	public void setVirtualNotifications(Short virtualNotifications) {
		this.virtualNotifications = virtualNotifications;
	}

	
}
