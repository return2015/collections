package com.returnsoft.generic.util;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.inject.Singleton;
@Named
@Singleton
public class FacesUtil implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5681511053396545948L;

	public void sendErrorMessage(String messageSummary) {

		FacesMessage msg = new FacesMessage(messageSummary, "");
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	
	public void sendErrorMessage(String messageSummary, String messageDetail) {

		FacesMessage msg = new FacesMessage(messageSummary, messageDetail);
		msg.setSeverity(FacesMessage.SEVERITY_ERROR);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void sendConfirmMessage(String messageSummary, String messageDetail) {

		FacesMessage msg = new FacesMessage(messageSummary, messageDetail);
		msg.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void sendConfirmMessage(String messageSummary) {

		FacesMessage msg = new FacesMessage(messageSummary, null);
		msg.setSeverity(FacesMessage.SEVERITY_INFO);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

}
