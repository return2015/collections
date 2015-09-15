package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.UserService;




@ManagedBean
@ViewScoped
public class EditUserController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private User userSelected;

	@EJB
	private UserService userService;

	private List<SelectItem> userTypes;
	private String userTypeSelected;
	
	private List<SelectItem> banks;
	private List<String> banksSelected;

	@PostConstruct
	public void initialize() {
		try {

			System.out.println("Ingreso a initialize");

			String userId = FacesContext.getCurrentInstance()
					.getExternalContext().getRequestParameterMap()
					.get("userId");

			System.out.println("userId:" + userId);
			userSelected = new User();
			userSelected.setId(Integer.parseInt(userId));

			userSelected = userService.findById(userSelected.getId());
			
			
			List<Bank> banksEntity = userService.getBanks();
			banks = new ArrayList<SelectItem>();
			for (Bank bank : banksEntity) {
				SelectItem item = new SelectItem();
				item.setLabel(bank.getName());
				item.setValue(bank.getId());
				banks.add(item);
			}

			

			//List<UserType> userTypesEntity = userService.getUserTypes();
			userTypes = new ArrayList<SelectItem>();
			for (UserTypeEnum userTypeEnum : UserTypeEnum.values()) {
				
				SelectItem item = new SelectItem();
				item.setValue(userTypeEnum.getId());
				item.setLabel(userTypeEnum.getName());
				userTypes.add(item);
			}
			/*for (UserType userType : userTypesEntity) {
				SelectItem item = new SelectItem();
				item.setLabel(userType.getName());
				item.setValue(userType.getId());
				userTypes.add(item);
			}*/
			
			if (userSelected.getUserType()!=null && userSelected.getUserType().getId()!=null) {
				userTypeSelected=userSelected.getUserType().getId().toString(); 
			}
			
			banksSelected = new ArrayList<String>();
			System.out.println("cantidad de banks: "+userSelected.getBanks().size());
			if (userSelected.getBanks()!=null && userSelected.getBanks().size()>0) {
				System.out.println("cantidad de banks: "+userSelected.getBanks().size());
				for (Bank bank : userSelected.getBanks()) {
					if (bank!=null && bank.getId()!=null) {
						banksSelected.add(bank.getId().toString());
					}
				}
			}
			
			

		} catch (Exception e) {

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	
	
	public void edit(){
		try {
			
			List<Bank> banksEntity = new ArrayList<Bank>();
			
			if (banksSelected != null && banksSelected.size() > 0) {
				for (String bankId : banksSelected) {
					Bank bankEntity = new Bank();
					bankEntity.setId(Short.parseShort(bankId));
					banksEntity.add(bankEntity);
				}
			}
			
			userSelected.setBanks(banksEntity);
			
			if (userTypeSelected!=null && userTypeSelected.length()>0) {
				UserTypeEnum userType = UserTypeEnum.findById(Short.parseShort(userTypeSelected));
				userSelected.setUserType(userType);
			}
			
			/*if (userTypeSelected!=null && userTypeSelected.length()>0) {
				UserType userType = new UserType();
				userType.setId(Integer.parseInt(userTypeSelected));
				userSelected.setUserType(userType);
			}*/
			
			userSelected = userService.update(userSelected);
			FacesMessage msg = new FacesMessage(
					"Se edito usuario con id: "
							+ userSelected.getId());
			msg.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, msg);
			
			
		} catch (Exception e) {

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	public User getUserSelected() {
		return userSelected;
	}

	public void setUserSelected(User userSelected) {
		this.userSelected = userSelected;
	}

	public List<SelectItem> getUserTypes() {
		return userTypes;
	}

	public void setUserTypes(List<SelectItem> userTypes) {
		this.userTypes = userTypes;
	}

	public String getUserTypeSelected() {
		return userTypeSelected;
	}

	public void setUserTypeSelected(String userTypeSelected) {
		this.userTypeSelected = userTypeSelected;
	}



	public List<SelectItem> getBanks() {
		return banks;
	}



	public void setBanks(List<SelectItem> banks) {
		this.banks = banks;
	}



	public List<String> getBanksSelected() {
		return banksSelected;
	}



	public void setBanksSelected(List<String> banksSelected) {
		this.banksSelected = banksSelected;
	}

	

}
