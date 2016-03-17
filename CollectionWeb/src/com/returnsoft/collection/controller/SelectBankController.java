package com.returnsoft.collection.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.service.BankService;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.util.FacesUtil;
import com.returnsoft.collection.util.SessionBean;

@ManagedBean
@ViewScoped
public class SelectBankController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7343165813110237696L;

	private String bankSelected;
	private List<SelectItem> banks;

	// private UIInput banksCOM;

	@EJB
	private UserService userService;

	@EJB
	private BankService bankService;

	@Inject
	private FacesUtil facesUtil;

	@Inject
	private SessionBean sessionBean;

	public SelectBankController() {
		// System.out.println("Construyendo SelectBankController");

	}

	public String initialize() {
		// System.out.println("Inicializando SelectBankController");
		try {

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			List<Bank> banksEntity = bankService.findByUser(sessionBean.getUser().getId());
			banks = new ArrayList<SelectItem>();
			for (Bank bank : banksEntity) {
				SelectItem item = new SelectItem();
				item.setLabel(bank.getName());
				item.setValue(bank.getId());
				banks.add(item);
			}
			// banksCOM.set

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

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() == null) {
				throw new UserLoggedNotFoundException();
			}

			List<Bank> banksEntity = bankService.findByUser(sessionBean.getUser().getId());
			banks = new ArrayList<SelectItem>();
			for (Bank bank : banksEntity) {
				SelectItem item = new SelectItem();
				item.setLabel(bank.getName());
				item.setValue(bank.getId());
				banks.add(item);
			}
			// banksCOM.set

			// return null;

		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
			// return "login.xhtml?faces-redirect=true";
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getMessage());
			// return null;
		}
	}

	public void selectBank() {
		try {

			System.out.println("bankSelected:" + bankSelected);

			if (sessionBean == null || sessionBean.getUser() == null || sessionBean.getUser().getId() < 1) {
				throw new UserLoggedNotFoundException();
			}

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

				// return "home?faces-redirect=true";

			}

		} catch (Exception e) {

			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(), e.getMessage());

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
