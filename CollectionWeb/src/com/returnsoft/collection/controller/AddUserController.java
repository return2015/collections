package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.generic.util.FacesUtil;
import com.returnsoft.generic.util.SessionBean;


@ManagedBean
@ViewScoped
public class AddUserController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private User userSelected;

	@EJB
	private UserService userService;
	
	@EJB
	private BankService bankService;

	private List<SelectItem> userTypes;
	private String userTypeSelected;
	
	private List<String> banksSelected;
	private List<SelectItem> banks;
	
	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;

	public AddUserController() {
		
		//facesUtil = new FacesUtil();
		//userSelected = new User();
		
	}

	public void reset() {
		
		userSelected = new User();
		userTypeSelected = null;
		banksSelected = null;
		
	}

	

	//@PostConstruct
	public String initialize() {
		try {

			//System.out.println("Ingreso a initialize");

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}
			
			userSelected = new User();
			userSelected.setIsActive(Boolean.TRUE);

			userTypes = new ArrayList<SelectItem>();
			for (UserTypeEnum userType : UserTypeEnum.values()) {
				SelectItem item = new SelectItem();
				item.setLabel(userType.getName());
				item.setValue(userType.getId());
				userTypes.add(item);
			}
			
			List<Bank> banksEntity = bankService.getAll();
			banks = new ArrayList<SelectItem>();
			for (Bank bank : banksEntity) {
				SelectItem item = new SelectItem();
				item.setValue(bank.getId().toString());
				item.setLabel(bank.getName());
				banks.add(item);
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

	
	

	public void add() {
		try {

			List<Bank> banksEntity = new ArrayList<Bank>();
			
			if (userTypeSelected!=null && userTypeSelected.length()>0) {
				UserTypeEnum userType = UserTypeEnum.findById(Short.parseShort(userTypeSelected));
				userSelected.setUserType(userType);
			}
			

			/*if (userTypeSelected != null && userTypeSelected.length() > 0) {
				UserType userType = new UserType();
				userType.setId(Integer.parseInt(userTypeSelected));
				userSelected.setUserType(userType);
			}*/
			
			if (banksSelected != null && banksSelected.size() > 0) {
				for (String bankId : banksSelected) {
					Bank bankEntity = new Bank();
					bankEntity.setId(Short.parseShort(bankId));
					banksEntity.add(bankEntity);
				}
			}

			userSelected.setBanks(banksEntity);

			userService.add(userSelected);

			// SE IMPRIME MENSAJE DE CONFIRMACION
			FacesMessage msg = new FacesMessage(
					"Se creó satisfactoriamente el usuario ");
			msg.setSeverity(FacesMessage.SEVERITY_INFO);
			FacesContext.getCurrentInstance().addMessage(null, msg);

			reset();

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());
			
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
