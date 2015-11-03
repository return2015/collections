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

import org.primefaces.context.RequestContext;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.service.MaintenanceService;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class AddMaintenanceController implements Serializable{

	/**
	 * aca un mensaje
	 */
	private static final long serialVersionUID = 1L;
	
	private SaleState maintenanceSelected;
	
	//private List<SelectItem> saleStates;
	
	//private final String[] saleStates = { "ACTIVO", "BAJA" };
	
	private List<SelectItem> saleStates;
	private String saleStateSelected;
	
	@EJB
	private MaintenanceService maintenanceService;
	
	@EJB
	private SaleService saleService;
	
	private FacesUtil facesUtil;
	
	@PostConstruct
	public void initialize() {
		System.out.println("ingreso a initialize en AddMaintenance");
		try {

			System.out.println("Ingreso a initialize");
			
			String saleId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("saleId");
			
			Sale saleSelected = saleService.findById(Long.parseLong(saleId));
			
			maintenanceSelected = new SaleState();
			Sale sale = new Sale();
			sale.setId(saleSelected.getId());
			maintenanceSelected.setSale(sale);
			maintenanceSelected.setDownUser(saleSelected.getSaleState().getDownUser());
			maintenanceSelected.setDownChannel(saleSelected.getSaleState().getDownChannel());
			maintenanceSelected.setDownObservation(saleSelected.getSaleState().getDownObservation());
			maintenanceSelected.setDownReason(saleSelected.getSaleState().getDownReason());
			maintenanceSelected.setDate(saleSelected.getSaleState().getDate());
			//maintenanceSelected.setCode(saleSelected.getCode());
			
			if (saleSelected.getSaleState().getState()!=null) {
				
				saleStateSelected = saleSelected.getSaleState().getState().getId().toString();
				
			}
			
			updateSaleStates();
			

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
					
			
			if (saleStateSelected != null && saleStateSelected.length() > 0) {
				maintenanceSelected.setState(SaleStateEnum.findById(Short.parseShort(saleStateSelected)));
			}
			
			maintenanceSelected.setCreatedAt(current);
			maintenanceSelected.setCreatedBy(user);
			
			maintenanceService.add(maintenanceSelected);
			
			Sale saleReturn = saleService.findById(maintenanceSelected.getSale().getId());
			
			RequestContext.getCurrentInstance().closeDialog(saleReturn);

		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
		}

	}
	
	public void updateSaleStates(){
		
		saleStates = new ArrayList<SelectItem>();
		
			for (SaleStateEnum saleStateEnum : SaleStateEnum.values()) {
				
				SelectItem item = new SelectItem();
				item.setValue(saleStateEnum.getId());
				item.setLabel(saleStateEnum.getName());
				saleStates.add(item);
			}
			
	}



	public SaleState getMaintenanceSelected() {
		return maintenanceSelected;
	}



	public void setMaintenanceSelected(SaleState maintenanceSelected) {
		this.maintenanceSelected = maintenanceSelected;
	}






	public List<SelectItem> getSaleStates() {
		return saleStates;
	}



	public void setSaleStates(List<SelectItem> saleStates) {
		this.saleStates = saleStates;
	}



	public String getSaleStateSelected() {
		return saleStateSelected;
	}



	public void setSaleStateSelected(String saleStateSelected) {
		this.saleStateSelected = saleStateSelected;
	}
	
	
	

}
