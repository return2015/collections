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

import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.enumeration.UserTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.exception.UserLoggedNotFoundException;
import com.returnsoft.collection.exception.UserPermissionNotFoundException;
import com.returnsoft.collection.service.UserService;
import com.returnsoft.collection.util.FacesUtil;

@ManagedBean
@ViewScoped
public class SelectBankController implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7343165813110237696L;
	
	private String bankSelected;
	private List<SelectItem> banks;
	
	@EJB
	private UserService userService;
	
	private FacesUtil facesUtil;
	
	public SelectBankController(){
		
		System.out.println("Construyendo SelectBankController");
		facesUtil = new FacesUtil();
	}
	
	
	public String initialize(){
		
		System.out.println("Inicializando SelectBankController");
		
		try {
			
			SessionBean sessionBean = (SessionBean) FacesContext
					.getCurrentInstance().getExternalContext().getSessionMap()
					.get("sessionBean");
			
			/*Integer userId = (Integer) FacesContext.getCurrentInstance()
					.getExternalContext().getSessionMap().get("userId");*/
			
			if (sessionBean!=null && sessionBean.getUser()!=null && sessionBean.getUser().getId()>0) {
				
				if (!sessionBean.getUser().getUserType().equals(UserTypeEnum.ADMIN)
						&& !sessionBean.getUser().getUserType().equals(UserTypeEnum.AGENT)) {
					throw new UserPermissionNotFoundException();
				}
					
					//sessionBean.setCurrentPage("home");
					/*FacesContext.getCurrentInstance().getExternalContext()
							.getSessionMap().put("sessionBean", sessionBean);*/
				List<Bank> banksEntity = userService.findBanksByUserId(sessionBean.getUser().getId());
				banks = new ArrayList<SelectItem>();
				for (Bank bank : banksEntity) {
					SelectItem item = new SelectItem();
					item.setLabel(bank.getName());
					item.setValue(bank.getId());
					banks.add(item);
				}
				
				return null;
				
			} else{
				throw new UserLoggedNotFoundException();
			}
			
			
		} catch (UserLoggedNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";
		} catch (UserPermissionNotFoundException e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return "login.xhtml?faces-redirect=true";		
		} catch (Exception e) {
			e.printStackTrace();
			facesUtil.sendErrorMessage(e.getClass().getSimpleName(),
					e.getMessage());
			return null;
		}
	}
	
	public String selectBank(){
		try {
			
			if (bankSelected!=null && bankSelected.length()>0) {
				
				Bank bank = userService.findBankById(Short.parseShort(bankSelected));
				
				SessionBean sessionBean = (SessionBean) FacesContext
						.getCurrentInstance().getExternalContext().getSessionMap()
						.get("sessionBean");
				
				/*FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().put("bankId", bank.getId());
				
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().put("bankName", bank.getName());	*/
				sessionBean.setBank(bank);
				/*FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().put("isAdmin", true);*/
				FacesContext.getCurrentInstance().getExternalContext()
				.getSessionMap().put("sessionBean", sessionBean);
				
				return "home?faces-redirect=true";
				
			}else{
				return null;
			}
			
			
			
		} catch (Exception e) {

			if (!(e instanceof ServiceException)) {
				e.printStackTrace();
			}
			FacesMessage msg = new FacesMessage(e.getMessage(), e.getMessage());
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
			return null;
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
