package com.returnsoft.collection.util;

import java.util.Map;
import java.util.Set;

import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.PartialViewContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class LifeCycleListener implements PhaseListener {
	/**
	* 
	*/
	private static final long serialVersionUID = -1351679285755340281L;

	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}

	public void beforePhase(PhaseEvent event) {
		System.out.println("START PHASE " + event.getPhaseId());
		/*
		 * for (String param :
		 * event.getFacesContext().getExternalContext().getRequestMap().keySet()
		 * ) { System.out.println("param"+param); }
		 */
		
		/*if (event.getFacesContext().getViewRoot()==null) {
			System.out.println("viewroot es nulo");
		}else{
			System.out.println("viewroot "+event.getFacesContext().getViewRoot().getClientId());
		}
		if (event.getFacesContext().getAttributes()!=null) {
			System.out.println("canti"+event.getFacesContext().getAttributes().size());
			System.out.println(event.getFacesContext().getAttributes().toString());
		}else{
			System.out.println("atrr is null");
		}*/
		
		
		

	}

	public void afterPhase(PhaseEvent event) {
		System.out.println("END PHASE " + event.getPhaseId());

		UIViewRoot uivr = event.getFacesContext().getViewRoot();
		
		//System.out.println(event.toString());

		/*if (uivr.findComponent("form:searchType") != null) {
			javax.faces.component.html.HtmlSelectOneMenu com = (javax.faces.component.html.HtmlSelectOneMenu) uivr
					.findComponent("form:searchType");
			if (com.getLocalValue() != null) {
				System.out.println("form:searchType.getLocalValue() " + com.getLocalValue());
			} else {
				System.out.println("form:searchType.getLocalValue() es nulo");
			}
			if (com.getValue() != null) {
				System.out.println("form:searchType.getValue() " + com.getValue());
			} else {
				System.out.println("form:searchType.getValue() es nulo");
			}
			if (com.getSubmittedValue() != null) {
				System.out.println("form:searchType.getSubmittedValue() " + com.getSubmittedValue());
			} else {
				System.out.println("form:searchType.getSubmittedValue() es nulo");
			}
		} else {
			System.out.println("form:searchType es nulo");
		}*/
		
		if (uivr.findComponent("form:personType") != null) {
			javax.faces.component.html.HtmlSelectOneMenu com = (javax.faces.component.html.HtmlSelectOneMenu) uivr
					.findComponent("form:personType");
			if (com.getLocalValue() != null) {
				System.out.println("form:personType.getLocalValue() " + com.getLocalValue());
			} else {
				System.out.println("form:personType.getLocalValue() es nulo");
			}
			if (com.getValue() != null) {
				System.out.println("form:personType.getValue() " + com.getValue());
			} else {
				System.out.println("form:personType.getValue() es nulo");
			}
			if (com.getSubmittedValue() != null) {
				System.out.println("form:personType.getSubmittedValue() " + com.getSubmittedValue());
			} else {
				System.out.println("form:personType.getSubmittedValue() es nulo");
			}
		} else {
			System.out.println("form:personType es nulo");
		}
		
		/*if (event.getFacesContext().getApplication()!=null) {
			System.out.println("app"+event.getFacesContext().getApplication().getActionListener().toString());
			System.out.println("app"+event.getFacesContext().getApplication().getELContextListeners().toString());
		}else{
			System.out.println("app is null");
		}*/
		

		PartialViewContext pvc = event.getFacesContext().getPartialViewContext();
		if (pvc != null) {
			System.out.println(pvc.getExecuteIds());
			System.out.println(pvc.getRenderIds());
			System.out.println(pvc.isAjaxRequest());
			System.out.println(pvc.isExecuteAll());
			System.out.println(pvc.isPartialRequest());
			System.out.println(pvc.isRenderAll());
			System.out.println(pvc.isResetValues());
			System.out.println(pvc.getPartialResponseWriter().getContentType());

		}
		//System.out.println("rpvm"+event.getFacesContext().getExternalContext().getRequestParameterValuesMap().size());
		System.out.println("rpvm"+event.getFacesContext().getExternalContext().getRequestParameterValuesMap().toString());
		
		System.out.println("rpvm"+event.getFacesContext().getExternalContext().getRequestParameterNames().toString());
		
		//System.out.println("rpvm"+event.getFacesContext().getExternalContext().getRequestParameterMap().size());
		System.out.println("rpvm"+event.getFacesContext().getExternalContext().getRequestParameterMap().toString());
		
		
		

	}

}
