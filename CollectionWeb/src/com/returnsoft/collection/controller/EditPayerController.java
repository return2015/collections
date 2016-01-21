package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.PayerService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;
import com.returnsoft.collection.util.SessionBean;

@ManagedBean
@ViewScoped
public class EditPayerController implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7014651477691666793L;
	
	private Payer payerSelected;
	
	@EJB
	private PayerService payerService;
	
	@EJB
	private SaleService saleService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SessionBean sessionBean;
	
	public EditPayerController(){
		
		//facesUtil = new FacesUtil();
	}
	
	
	public String initialize() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			String saleId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("saleId");
			
			Sale saleSelected = saleService.findById(Long.parseLong(saleId));
			
			//payerSelected = new Payer();
			payerSelected = saleSelected.getPayer();
			//Sale sale = new Sale();
			//sale.setId(saleSelected.getId());
			//payerSelected.setSale(sale);
			/*payerSelected.setAddress(saleSelected.getPayer().getAddress());
			payerSelected.setDepartment(saleSelected.getPayer().getDepartment());
			payerSelected.setDistrict(saleSelected.getPayer().getDistrict());
			payerSelected.setFirstnameResponsible(saleSelected.getPayer().getFirstnameResponsible());
			payerSelected.setLastnameMaternalResponsible(saleSelected.getPayer().getLastnameMaternalResponsible());
			payerSelected.setLastnamePaternalResponsible(saleSelected.getPayer().getLastnamePaternalResponsible());
			payerSelected.setMail(saleSelected.getPayer().getMail());
			payerSelected.setNuicResponsible(saleSelected.getPayer().getNuicResponsible());
			payerSelected.setProvince(saleSelected.getPayer().getProvince());*/
			
			return null;
			
		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			return null;
		}

	}
	
	
	public void update() {
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
			
			/*SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext()
					.getSessionMap().get("sessionBean");*/
					
			//Integer userId = sessionBean.getUser().getId();
			User user = sessionBean.getUser();
			//user.setId(userId);
			Date current = new Date();
			
			payerSelected.setUpdatedAt(current);
			payerSelected.setUpdatedBy(user);
			
			payerService.update(payerSelected);
			
			// RETORNA VENTA ACTUALIZADA
			
			Sale saleReturn = saleService.findById(payerSelected.getId());
			
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
