package com.returnsoft.collection.controller;

import java.io.Serializable;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.returnsoft.collection.service.LoteService;

@ManagedBean
@ViewScoped
public class SearchLoteController implements Serializable{
	
	@EJB
	private LoteService loteService;
	
	public SearchLoteController(){
		
	}

}
