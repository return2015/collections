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
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity 
@Table(name = "repayment")
public class Repayment implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rep_id")
	private Integer id;
	
	@Transient
	private String code;
	
	/*@Transient
	private String receiptNumber;*/
	
	
	@Column(name = "rep_insurance_premium_number")
	private Integer insurancePremiumNumber;
	
	@Column(name = "rep_charge_amount")
	private BigDecimal chargeAmount;
	
	@Column(name = "rep_returned_amount")
	private BigDecimal returnedAmount;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rep_returned_date")
	private Date returnedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rep_payment_date")
	private Date paymentDate;
	
	
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rep_created_at")
	private Date createdAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "rep_created_by")
	private User createdBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "rep_sal_id")
	private Sale sale;
	
	/*@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "rep_col_id")
	private Collection collection;*/
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getReturnedDate() {
		return returnedDate;
	}

	public void setReturnedDate(Date returnedDate) {
		this.returnedDate = returnedDate;
	}

	

	/*public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}*/

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public BigDecimal getReturnedAmount() {
		return returnedAmount;
	}

	public void setReturnedAmount(BigDecimal returnedAmount) {
		this.returnedAmount = returnedAmount;
	}

	public Integer getInsurancePremiumNumber() {
		return insurancePremiumNumber;
	}

	public void setInsurancePremiumNumber(Integer insurancePremiumNumber) {
		this.insurancePremiumNumber = insurancePremiumNumber;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	/*public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}*/

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
	
	
	

}
