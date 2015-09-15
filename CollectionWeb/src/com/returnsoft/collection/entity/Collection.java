package com.returnsoft.collection.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;

import com.returnsoft.collection.converter.CollectionResponseConverter;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;

@Entity
@Table(name = "collection")
public class Collection implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "col_id")
	private Long id;
	
	@Transient
	private String saleCode;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_affiliation_date")
	private Date affiliationDate;
	
	@Column(name = "col_maximum_amount")
	private BigDecimal maximumAmount;
	
	@Column(name = "col_charge_amount")
	private BigDecimal chargeAmount;
	
	@Column(name = "col_receipt_number")
	private String receiptNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_credit_card_updated")
	private Date creditCardUpdated;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_estimated_date")
	private Date estimatedDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_authorization_date")
	private Date authorizationDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_deposit_date")
	private Date depositDate;
	
	@Column(name = "col_response_code")
	private Short responseCode;
	
	@Column(name = "col_authorization_code")
	private Integer authorizationCode;
	
	@Column(name = "col_response_message")
	@Convert(converter=CollectionResponseConverter.class)
	private CollectionResponseEnum responseMessage;
	
	@Column(name = "col_action")
	private String action;
	
	@Column(name = "col_transaction_state")
	private String transactionState;
	
	@Column(name = "col_lote_number")
	private Integer loteNumber;
	
	@Column(name = "col_channel")
	private String channel;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_disaffiliation_date")
	private Date disaffiliationDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_created_at")
	private Date createdAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "col_created_by")
	private User createdBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "col_sal_id")
	private Sale sale;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Date getAffiliationDate() {
		return affiliationDate;
	}

	public void setAffiliationDate(Date affiliationDate) {
		this.affiliationDate = affiliationDate;
	}


	public BigDecimal getMaximumAmount() {
		return maximumAmount;
	}

	public void setMaximumAmount(BigDecimal maximumAmount) {
		this.maximumAmount = maximumAmount;
	}

	public BigDecimal getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(BigDecimal chargeAmount) {
		this.chargeAmount = chargeAmount;
	}

	public String getReceiptNumber() {
		return receiptNumber;
	}

	public void setReceiptNumber(String receiptNumber) {
		this.receiptNumber = receiptNumber;
	}

	public Date getCreditCardUpdated() {
		return creditCardUpdated;
	}

	public void setCreditCardUpdated(Date creditCardUpdated) {
		this.creditCardUpdated = creditCardUpdated;
	}

	public Date getEstimatedDate() {
		return estimatedDate;
	}

	public void setEstimatedDate(Date estimatedDate) {
		this.estimatedDate = estimatedDate;
	}

	public Date getAuthorizationDate() {
		return authorizationDate;
	}

	public void setAuthorizationDate(Date authorizationDate) {
		this.authorizationDate = authorizationDate;
	}

	public Date getDepositDate() {
		return depositDate;
	}

	public void setDepositDate(Date depositDate) {
		this.depositDate = depositDate;
	}



	public Short getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Short responseCode) {
		this.responseCode = responseCode;
	}

	public Integer getAuthorizationCode() {
		return authorizationCode;
	}

	public void setAuthorizationCode(Integer authorizationCode) {
		this.authorizationCode = authorizationCode;
	}

	

	public CollectionResponseEnum getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(CollectionResponseEnum responseMessage) {
		this.responseMessage = responseMessage;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTransactionState() {
		return transactionState;
	}

	public void setTransactionState(String transactionState) {
		this.transactionState = transactionState;
	}

	public Integer getLoteNumber() {
		return loteNumber;
	}

	public void setLoteNumber(Integer loteNumber) {
		this.loteNumber = loteNumber;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	/*public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}*/

	public Date getDisaffiliationDate() {
		return disaffiliationDate;
	}

	public void setDisaffiliationDate(Date disaffiliationDate) {
		this.disaffiliationDate = disaffiliationDate;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public String getSaleCode() {
		return saleCode;
	}

	public void setSaleCode(String saleCode) {
		this.saleCode = saleCode;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	
	
	
	

}
