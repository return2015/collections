package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.service.SaleStateService;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;

@ManagedBean
@ViewScoped
public class EditSaleStateController implements Serializable{

	/**
	 * aca un mensaje
	 */
	private static final long serialVersionUID = 1L;
	
	private SaleState saleStateSelected;
	
	//private List<SelectItem> saleStates;
	
	//private final String[] saleStates = { "ACTIVO", "BAJA" };
	
	private List<SelectItem> states;
	private String stateSelected;
	
	@EJB
	private SaleStateService saleStateService;
	
	@EJB
	private SaleService saleService;
	
	@Inject
	private FacesUtil facesUtil;
	
	@Inject
	private SessionBean sessionBean;
	
	//@PostConstruct
	public String initialize() {
		//System.out.println("ingreso a initialize en AddMaintenance");
		try {
			
			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			//System.out.println("Ingreso a initialize");
			
			String saleId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("saleId");
			
			Sale saleSelected = saleService.findById(Long.parseLong(saleId));
			
			saleStateSelected = saleSelected.getSaleState();
			//Sale sale = new Sale();
			//sale.setId(saleSelected.getId());
			/*saleStateSelected.
			maintenanceSelected.setDownUser(saleSelected.getSaleState().getDownUser());
			maintenanceSelected.setDownChannel(saleSelected.getSaleState().getDownChannel());
			maintenanceSelected.setDownObservation(saleSelected.getSaleState().getDownObservation());
			maintenanceSelected.setDownReason(saleSelected.getSaleState().getDownReason());
			maintenanceSelected.setDate(saleSelected.getSaleState().getDate());*/
			//maintenanceSelected.setCode(saleSelected.getCode());
			
			if (saleSelected.getSaleState().getState()!=null) {
				
				stateSelected = saleSelected.getSaleState().getState().getId().toString();
				
			}
			
			states = new ArrayList<SelectItem>();
			
			for (SaleStateEnum saleStateEnum : SaleStateEnum.values()) {
				
				SelectItem item = new SelectItem();
				item.setValue(saleStateEnum.getId());
				item.setLabel(saleStateEnum.getName());
				states.add(item);
			}
			
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
					
			
			if (stateSelected != null && stateSelected.length() > 0) {
				saleStateSelected.setState(SaleStateEnum.findById(Short.parseShort(stateSelected)));
			}
			
			saleStateSelected.setUpdatedAt(current);
			saleStateSelected.setUpdatedBy(user);
			
			saleStateService.update(saleStateSelected);
			
			Sale saleReturn = saleService.findById(saleStateSelected.getId());
			
			RequestContext.getCurrentInstance().closeDialog(saleReturn);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}



	public SaleState getSaleStateSelected() {
		return saleStateSelected;
	}



	public void setSaleStateSelected(SaleState saleStateSelected) {
		this.saleStateSelected = saleStateSelected;
	}



	public List<SelectItem> getStates() {
		return states;
	}



	public void setStates(List<SelectItem> states) {
		this.states = states;
	}



	public String getStateSelected() {
		return stateSelected;
	}



	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}
	
	/*public void updateSaleStates(){
		
		saleStates = new ArrayList<SelectItem>();
		
			for (SaleStateEnum saleStateEnum : SaleStateEnum.values()) {
				
				SelectItem item = new SelectItem();
				item.setValue(saleStateEnum.getId());
				item.setLabel(saleStateEnum.getName());
				saleStates.add(item);
			}
			
	}*/



	





	
	

}
