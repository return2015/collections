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
	private Long id;
	
	@Column(name = "salsta_state")
	@Convert(converter=SaleStateConverter.class)
	private SaleStateEnum state;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "salsta_date")
	private Date date;
	
	@Column(name = "salsta_user")
	private String user;
	
	@Column(name = "salsta_channel")
	private String channel;
	
	@Column(name = "salsta_reason")
	private String reason;
	
	@Column(name = "salsta_observation")
	private String observation;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "salsta_lot_id")
	private Lote lote;
	
	
	@MapsId 
    @OneToOne()
	@JoinColumn(name = "salsta_id") 
	private Sale sale;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "salsta_updated_at")
	private Date updatedAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "salsta_updated_by")
	private User updatedBy;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	

	

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getObservation() {
		return observation;
	}

	public void setObservation(String observation) {
		this.observation = observation;
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

	public Lote getLote() {
		return lote;
	}

	public void setLote(Lote lote) {
		this.lote = lote;
	}

	
	
	
	

}
