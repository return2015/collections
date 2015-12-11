package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.util.FacesUtil;

@Named
@RequestScoped
public class SelectBankController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7343165813110237696L;

	private String bankSelected;
	private List<SelectItem> banks;
	
	//private UIInput banksCOM;

	@EJB
	private UserService userService;

	@EJB
	private BankService bankService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;

	public SelectBankController() {
		System.out.println("Construyendo SelectBankController");
	}

	public String initialize() {
		System.out.println("Inicializando SelectBankController");
		try {
			if (sessionBean == null && sessionBean.getUser() == null && sessionBean.getUser().getId() < 0) {
				throw new UserLoggedNotFoundException();
			}else {
				List<Bank> banksEntity = bankService.findByUser(sessionBean.getUser().getId());
				banks = new ArrayList<SelectItem>();
				for (Bank bank : banksEntity) {
					SelectItem item = new SelectItem();
					item.setLabel(bank.getName());
					item.setValue(bank.getId());
					banks.add(item);
				}
				//banksCOM.set
			}
			return null;

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
			return null;
		}
	}

	@PostConstruct
	public void start() {
		try {


			if (sessionBean == null && sessionBean.getUser() == null && sessionBean.getUser().getId() < 0) {
				throw new UserLoggedNotFoundException();
			} else {
				List<Bank> banksEntity = bankService.findByUser(sessionBean.getUser().getId());
				banks = new ArrayList<SelectItem>();
				for (Bank bank : banksEntity) {
					SelectItem item = new SelectItem();
					item.setLabel(bank.getName());
					item.setValue(bank.getId());
					banks.add(item);
				}
			}

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
		}
	}

	public void selectBank() {
		try {

			System.out.println("bankSelected:" + bankSelected);
			

			if (bankSelected != null && bankSelected.length() > 0) {

				Bank bank = bankService.findById(Short.parseShort(bankSelected));

				/*
				 * SessionBean sessionBean = (SessionBean) FacesContext
				 * .getCurrentInstance().getExternalContext().getSessionMap()
				 * .get("sessionBean");
				 */

				/*
				 * FacesContext.getCurrentInstance().getExternalContext()
				 * .getSessionMap().put("bankId", bank.getId());
				 * 
				 * FacesContext.getCurrentInstance().getExternalContext()
				 * .getSessionMap().put("bankName", bank.getName());
				 */
				sessionBean.setBank(bank);
				/*
				 * FacesContext.getCurrentInstance().getExternalContext()
				 * .getSessionMap().put("isAdmin", true);
				 */
				/*
				 * FacesContext.getCurrentInstance().getExternalContext()
				 * .getSessionMap().put("sessionBean", sessionBean);
				 */

				//return "home?faces-redirect=true";

			}
			

		} catch (NullPointerException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage("Existen valores nulos");
			//return null;
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
			//turn null;
		}
	}

	public String getBankSelected() {
		return bankSelected;
	}

	public void setBankSelected(String bankSelected) {
		this.bankSelected = bankSelected;
	}

	public List<SelectItem> getBanks() {
		return banks;
	}

	public void setBanks(List<SelectItem> banks) {
		this.banks = banks;
	}

	

	

}
