package com.returnsoft.collection.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.inject.Singleton;
@Named
@Singleton
public class FacesUtil {
	
	
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
