package com.returnsoft.collection.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lot_date")
	private Date date;
	
	@Column(name = "lot_errors")
	private String errors;
	
	

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

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

	public String getErrors() {
		return errors;
	}

	public void setErrors(String errors) {
		this.errors = errors;
	}
	
	
	

}
