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

import com.returnsoft.collection.converter.NotificationStateConverter;
import com.returnsoft.collection.converter.NotificationTypeConverter;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
@Entity
@Table(name = "notification")
public class Notification implements Serializable{

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7542600335130604867L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "not_id")
	private Integer id;
	
	/*@Column(name = "not_code")
	private String code;*/
	
	@Column(name = "not_charge_number")
	private String chargeNumber;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "not_sending_at")
	private Date sendingAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "not_answering_at")
	private Date answeringAt;
	
	@Column(name = "not_sending_file")
	private String sendingFile;
	
	@Column(name = "not_answering_file")
	private String answeringFile;
	
	@Column(name="not_type")
	@Convert(converter=NotificationTypeConverter.class)
	private NotificationTypeEnum type;
	
	@Column(name="not_state")
	@Convert(converter=NotificationStateConverter.class)
	private NotificationStateEnum state;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "not_created_by")
	private User createdBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "not_updated_by")
	private User updatedBy;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "not_sal_id")
	private Sale sale;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "not_created_at")
	private Date createdAt;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "not_updated_at")
	private Date updatedAt;
	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	

	public String getChargeNumber() {
		return chargeNumber;
	}

	public void setChargeNumber(String chargeNumber) {
		this.chargeNumber = chargeNumber;
	}

	public Date getSendingAt() {
		return sendingAt;
	}

	public void setSendingAt(Date sendingAt) {
		this.sendingAt = sendingAt;
	}

	public Date getAnsweringAt() {
		return answeringAt;
	}

	public void setAnsweringAt(Date answeringAt) {
		this.answeringAt = answeringAt;
	}



	

	public NotificationTypeEnum getType() {
		return type;
	}

	public void setType(NotificationTypeEnum type) {
		this.type = type;
	}

	public NotificationStateEnum getState() {
		return state;
	}

	public void setState(NotificationStateEnum state) {
		this.state = state;
	}

	public String getSendingFile() {
		return sendingFile;
	}

	public void setSendingFile(String sendingFile) {
		this.sendingFile = sendingFile;
	}

	public String getAnsweringFile() {
		return answeringFile;
	}

	public void setAnsweringFile(String answeringFile) {
		this.answeringFile = answeringFile;
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

	public Date getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public Sale getSale() {
		return sale;
	}

	public void setSale(Sale sale) {
		this.sale = sale;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}


	
	
	

}
