package com.returnsoft.collection.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

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
	
	
	////////////////
	
	
	@Column(name = "sal_account_number")
	private Long accountNumber;
	
	
	////////////////////////////
	
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
	private Date date;
	
	@Column(name = "sal_channel")
	private String channel;
	
	@Column(name = "sal_place")
	private String place;
	
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
	
	/*@Column(name = "sal_collection_period")
	private String collectionPeriod;*/
	
	
	
	@Column(name = "sal_collection_type")
	private String collectionType;
	
	@Column(name = "sal_insurance_premium")
	private BigDecimal insurancePremium;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sal_audit_date")
	private Date auditDate;
	
	@Column(name = "sal_audit_user")
	private String auditUser;
	
	
	
	/*@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_com_id")
	private Commerce commerce;*/
	
	@Column(name = "sal_commerce_code")
	private String commerceCode;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_colper_id")
	private CollectionPeriod collectionPeriod;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_pro_id")
	private Product product;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_ban_id")
	private Bank bank;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_lot_id")
	private Lote lote;
	
	/*@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_paymet_id")
	private PaymentMethod paymentMethod;*/
	
	////////////////////////////////////////////////
	
	/*@Column(name = "sal_affiliation")
	private Boolean affiliation;*/
	
	/*@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sal_affiliation_date")
	private Date affiliationDate;*/
	
	/*@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_affiliation_usr_id")
	private User affiliationUser;*/
	
	@Column(name = "sal_physical_notifications")
	//@Transient
	private Short physicalNotifications;
	
	@Column(name = "sal_virtual_notifications")
	//@Transient
	private Short virtualNotifications;
	
	@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_not_id")
	//@Transient
	private Notification notification;
	
	///////////////////////////////////////////
	
	//@OneToOne(fetch=FetchType.LAZY)
	//@JoinColumn(name = "sal_crecar_id")
	//@OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	//@PrimaryKeyJoinColumn
	@OneToOne(cascade= CascadeType.ALL,mappedBy="sale")
	private CreditCard creditCard;
	
	//@OneToOne(fetch=FetchType.LAZY)
	//@JoinColumn(name = "sal_salsta_id")
	//@OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
	//@PrimaryKeyJoinColumn
	@OneToOne(cascade= CascadeType.ALL,mappedBy="sale")
	private SaleState saleState;
	
	//@OneToOne(fetch=FetchType.LAZY,mappedBy="sale")
	/*@OneToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "sal_pay_id")*/
	//@OneToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.EAGER, orphanRemoval = true)
    // @JoinColumn(name="USER_ID", nullable=false)
    //@PrimaryKeyJoinColumn
	@OneToOne(cascade= CascadeType.ALL,mappedBy="sale")
	private Payer payer;
	
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

	/*public String getDocumentType() {
		return documentType;
	}

	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}*/

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

	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
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

	

	public CollectionPeriod getCollectionPeriod() {
		return collectionPeriod;
	}

	public void setCollectionPeriod(CollectionPeriod collectionPeriod) {
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

	

	/*public User getAffiliationUser() {
		return affiliationUser;
	}

	public void setAffiliationUser(User affiliationUser) {
		this.affiliationUser = affiliationUser;
	}*/

	

	public SaleState getSaleState() {
		return saleState;
	}

	public void setSaleState(SaleState saleState) {
		this.saleState = saleState;
	}

	/*public Commerce getCommerce() {
		return commerce;
	}

	public void setCommerce(Commerce commerce) {
		this.commerce = commerce;
	}*/
	
	

	/*public Notification getNotification() {
		return notification;
	}*/

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	

	/*public void setNotification(Notification notification) {
		this.notification = notification;
	}*/

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

	/*public Short getPhysicalNotifications() {
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
	}*/

	public String getCommerceCode() {
		return commerceCode;
	}

	public void setCommerceCode(String commerceCode) {
		this.commerceCode = commerceCode;
	}

	public Lote getLote() {
		return lote;
	}

	public void setLote(Lote lote) {
		this.lote = lote;
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

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}
	
	
	
}
