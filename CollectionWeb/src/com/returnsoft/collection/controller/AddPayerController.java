package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.service.PayerService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class AddPayerController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7014651477691666793L;
	
	private Payer payerSelected;
	
	@EJB
	private PayerService payerService;
	
	private FacesUtil facesUtil;
	
	public AddPayerController(){
		System.out.println("Ingreso a AddPayerController...");
		facesUtil = new FacesUtil();
	}
	
	
	public void initialize() {
		System.out.println("ingreso a initialize en AddPayer");
		try {

			System.out.println("Ingreso a initialize");
			
			String saleId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("saleId");
			
			Sale saleSelected = payerService.findSaleById(Long.parseLong(saleId));
			
			payerSelected = new Payer();
			Sale sale = new Sale();
			sale.setId(saleSelected.getId());
			payerSelected.setSale(sale);
			payerSelected.setAddress(saleSelected.getPayer().getAddress());
			payerSelected.setDepartment(saleSelected.getPayer().getDepartment());
			payerSelected.setDistrict(saleSelected.getPayer().getDistrict());
			payerSelected.setFirstnameResponsible(saleSelected.getPayer().getFirstnameResponsible());
			payerSelected.setLastnameMaternalResponsible(saleSelected.getPayer().getLastnameMaternalResponsible());
			payerSelected.setLastnamePaternalResponsible(saleSelected.getPayer().getLastnamePaternalResponsible());
			payerSelected.setMail(saleSelected.getPayer().getMail());
			payerSelected.setNuicResponsible(saleSelected.getPayer().getNuicResponsible());
			payerSelected.setProvince(saleSelected.getPayer().getProvince());
			
		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}
	}
	
	
	public void add() {
		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");
					
			Integer userId = sessionBean.getUser().getId();
			User user = new User();
			user.setId(userId);
			Date current = new Date();
			
			
			payerSelected.setCreatedAt(current);
			payerSelected.setCreatedBy(user);
			
			payerService.add(payerSelected);
			
			Sale saleReturn = payerService.findSaleById(payerSelected.getSale().getId());
			
			RequestContext.getCurrentInstance().closeDialog(saleReturn);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}


	public Payer getPayerSelected() {
		return payerSelected;
	}


	public void setPayerSelected(Payer payerSelected) {
		this.payerSelected = payerSelected;
	}
	
	
	

}
