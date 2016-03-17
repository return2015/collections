package com.returnsoft.collection.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
//import javax.persistence.Transient;

import com.returnsoft.collection.converter.CreditCardValidationConverter;
import com.returnsoft.collection.enumeration.CreditCardValidationEnum;

@Entity 
@Table(name = "credit_card")
public class CreditCard implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 935303972710761527L;


	@Id 
	//@Column(name = "crecar_id")
	private Long id;

	/*@Transient
	private String code;*/
	
	@Column(name = "crecar_number")
	private Long number;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "crecar_expiration_date")
	private Date expirationDate;
	
	@Column(name = "crecar_state")
	private String state;
	
	@Column(name = "crecar_days_default")
	private Integer daysOfDefault;

	@Column(name = "crecar_validation")
	@Convert(converter=CreditCardValidationConverter.class)
	private CreditCardValidationEnum validation;

	/*@OneToOne(fetch=FetchType.LAZY,mappedBy="creditCard")
	private Sale sale;*/
	
	@MapsId 
    @OneToOne
	@JoinColumn(name = "crecar_id") 
	private Sale sale;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "crecar_date")
	private Date date;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "crecar_updated_at")
	private Date updatedAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "crecar_updated_by")
	private User updatedBy;
	
	


	public CreditCard() {
		super();
		// TODO Auto-generated constructor stub
	}


	public CreditCard(Long number, Date expirationDate, String state, Integer daysOfDefault,
			 Sale sale, Date date) {
		super();
		this.number = number;
		this.expirationDate = expirationDate;
		this.state = state;
		this.daysOfDefault = daysOfDefault;
		this.sale = sale;
		this.date = date;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	/*public String getCode() {
		return code;
	}


	public void setCode(String code) {
		this.code = code;
	}*/


	public Long getNumber() {
		return number;
	}


	public void setNumber(Long number) {
		this.number = number;
	}


	public Date getExpirationDate() {
		return expirationDate;
	}


	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public Integer getDaysOfDefault() {
		return daysOfDefault;
	}


	public void setDaysOfDefault(Integer daysOfDefault) {
		this.daysOfDefault = daysOfDefault;
	}





	public CreditCardValidationEnum getValidation() {
		return validation;
	}


	public void setValidation(CreditCardValidationEnum validation) {
		this.validation = validation;
	}


	/*public Sale getSale() {
		return sale;
	}


	public void setSale(Sale sale) {
		this.sale = sale;
	}*/


	public Date getDate() {
		return date;
	}


	public void setDate(Date date) {
		this.date = date;
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


	public Sale getSale() {
		return sale;
	}


	public void setSale(Sale sale) {
		this.sale = sale;
	}


	/*public Date getCreatedAt() {
		return createdAt;
	}


	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}*/


	/*public User getCreatedBy() {
		return createdBy;
	}


	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}*/


	/*public Date getUpdateDate() {
		return updateDate;
	}


	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}*/




	


	
	
	
	

}
