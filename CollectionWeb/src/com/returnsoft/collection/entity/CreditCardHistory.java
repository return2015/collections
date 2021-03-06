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

import com.returnsoft.collection.converter.CreditCardValidationConverter;
import com.returnsoft.collection.enumeration.CreditCardValidationEnum;

@Entity 
@Table(name = "credit_card_history")
public class CreditCardHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3389616673926938400L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crecarhis_id")
	private Long idHist;
	
	/*@Column(name = "crecar_id")
	private Long id;*/
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "crecar_id")
	private Sale sale;
	
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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "crecar_date")
	private Date date;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "crecar_lot_id")
	private Lote lote;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "crecar_updated_at")
	private Date updatedAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "crecar_updated_by")
	private User updatedBy;

	public Long getIdHist() {
		return idHist;
	}

	public void setIdHist(Long idHist) {
		this.idHist = idHist;
	}

	/*public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}*/
	
	

	public Long getNumber() {
		return number;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
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

	public Lote getLote() {
		return lote;
	}

	public void setLote(Lote lote) {
		this.lote = lote;
	}


	

}
