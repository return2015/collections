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

import com.returnsoft.collection.converter.CollectionResponseConverter;
import com.returnsoft.collection.converter.MoneyTypeConverter;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.enumeration.MoneyTypeEnum;

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
	
	@Column(name = "col_maximum_amount")
	private BigDecimal maximumAmount;
	
	@Column(name = "col_charge_amount")
	private BigDecimal chargeAmount;
	
	@Column(name = "col_receipt_number")
	private String receiptNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_estimated_date")
	private Date estimatedDate;
	
	/*@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_authorization_date")
	private Date authorizationDate;*/
	
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
	
	@Column(name = "col_money_type_id")
	@Convert(converter=MoneyTypeConverter.class)
	private MoneyTypeEnum moneyType;
	
	/*@Column(name = "col_reason")
	private String reason;*/
	
	///////////////////
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "col_created_at")
	private Date createdAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "col_created_by")
	private User createdBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "col_sal_id")
	private Sale sale;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "col_payment_id")
	private PaymentMethod paymentMethod;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "col_lot_id")
	private Lote lote;
	
	

	public Collection() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Collection(BigDecimal maximumAmount, BigDecimal chargeAmount, 
			Date estimatedDate, Date depositDate, Short responseCode, Integer authorizationCode,
			CollectionResponseEnum responseMessage, String action, String transactionState, Integer loteNumber,
			String channel, MoneyTypeEnum moneyType,Sale sale, PaymentMethod paymentMethod) {
		super();
		this.maximumAmount = maximumAmount;
		this.chargeAmount = chargeAmount;
		this.estimatedDate = estimatedDate;
		this.depositDate = depositDate;
		this.responseCode = responseCode;
		this.authorizationCode = authorizationCode;
		this.responseMessage = responseMessage;
		this.action = action;
		this.transactionState = transactionState;
		this.loteNumber = loteNumber;
		this.channel = channel;
		this.moneyType = moneyType;
		
		this.sale = sale;
		this.paymentMethod = paymentMethod;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	

	public Date getEstimatedDate() {
		return estimatedDate;
	}

	public void setEstimatedDate(Date estimatedDate) {
		this.estimatedDate = estimatedDate;
	}

	
	public MoneyTypeEnum getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(MoneyTypeEnum moneyType) {
		this.moneyType = moneyType;
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

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public Lote getLote() {
		return lote;
	}

	public void setLote(Lote lote) {
		this.lote = lote;
	}

	
	
	
	
	

}
