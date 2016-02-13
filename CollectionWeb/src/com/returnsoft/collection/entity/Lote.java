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

import com.returnsoft.collection.converter.LoteTypeConverter;
import com.returnsoft.collection.enumeration.LoteTypeEnum;

@Entity
@Table(name = "lote")
public class Lote implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3240612294006661769L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lot_id")
	private Integer id;
	
	@Column(name = "lot_name")
	private String name;
	
	@Column(name = "lot_total")
	private Integer total;
	
	@Column(name = "lot_process")
	private Integer process;
	
	@Column(name = "lot_state")
	private String state;
	
	/*@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lot_date")
	private Date date;*/
	
	@Column(name = "lot_type_id")
	@Convert(converter = LoteTypeConverter.class)
	private LoteTypeEnum loteType;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "lot_created_by")
	private User createdBy;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lot_created_at")
	private Date createdAt;
	

	

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Integer getProcess() {
		return process;
	}

	public void setProcess(Integer process) {
		this.process = process;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public LoteTypeEnum getLoteType() {
		return loteType;
	}

	public void setLoteType(LoteTypeEnum loteType) {
		this.loteType = loteType;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	
	
	
	

}
