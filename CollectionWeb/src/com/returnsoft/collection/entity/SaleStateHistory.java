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
@Table(name = "sale_state_history")
public class SaleStateHistory implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9088742459381209639L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "salstahis_id")
	private Integer idHist;
	
	/*@Column(name = "salsta_id")
	private Integer id;*/
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "salsta_id")
	private Sale sale;
	
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
	
	/*@OneToOne(fetch=FetchType.LAZY,mappedBy="saleState")
	private Sale sale;*/
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "salsta_updated_at")
	private Date updatedAt;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "salsta_updated_by")
	private User updatedBy;

	public Integer getIdHist() {
		return idHist;
	}

	public void setIdHist(Integer idHist) {
		this.idHist = idHist;
	}

	
	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public SaleStateEnum getState() {
		return state;
	}

	public void setState(SaleStateEnum state) {
		this.state = state;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
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

	public Lote getLote() {
		return lote;
	}

	public void setLote(Lote lote) {
		this.lote = lote;
	}
	
	

}
