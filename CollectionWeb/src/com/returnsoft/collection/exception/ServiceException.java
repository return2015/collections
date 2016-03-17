package com.returnsoft.collection.exception;

import java.io.Serializable;
import java.util.List;

public class ServiceException extends Exception implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//private List<String> errors;
	
	/*public ServiceException(List<String> errors) {
		super("Error al consultar servicio remoto");
		this.errors=errors;
	}*/
	
	
	public ServiceException() {
		super("Error al consultar servicio remoto");
	}
	
	public ServiceException(String arg0) {
		super(arg0);
	}

	/*public ServiceException(Throwable arg0) {
		super(arg0);
	}
	
	public ServiceException(String arg0,Throwable arg1) {
		super(arg0,arg1);
	}*/


	/*public List<String> getErrors() {
		return errors;
	}


	public void setErrors(List<String> errors) {
		this.errors = errors;
	}*/
	

}
