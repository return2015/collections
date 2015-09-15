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

import com.returnsoft.collection.converter.SaleStateConverter;
import com.returnsoft.collection.enumeration.SaleStateEnum;
@Entity 
@Table(name = "sale_state")
public class SaleState implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -722923150396095288L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salsta_id")
	private Integer id;
	
	//@Transient
	//private String code;
	
	@Column(name = "salsta_state")
	@Convert(converter=SaleStateConverter.class)
	private SaleStateEnum state;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "salsta_date")
	private Date date;
	
	@Column(name = "salsta_user")
	private String downUser;
	
	@Column(name = "salsta_channel")
	private String downChannel;
	
	@Column(name = "salsta_reason")
	private String downReason;
	
	@Column(name = "salsta_observation")
	private String downObservation;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "salsta_created_at")
	private Date createdAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "salsta_created_by")
	private User createdBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "salsta_sal_id")
	private Sale sale;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}


	

	

	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public SaleStateEnum getState() {
		return state;
	}

	public void setState(SaleStateEnum state) {
		this.state = state;
	}

	public String getDownUser() {
		return downUser;
	}

	public void setDownUser(String downUser) {
		this.downUser = downUser;
	}

	public String getDownChannel() {
		return downChannel;
	}

	public void setDownChannel(String downChannel) {
		this.downChannel = downChannel;
	}

	public String getDownReason() {
		return downReason;
	}

	public void setDownReason(String downReason) {
		this.downReason = downReason;
	}

	public String getDownObservation() {
		return downObservation;
	}

	public void setDownObservation(String downObservation) {
		this.downObservation = downObservation;
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
	
	
	

}
