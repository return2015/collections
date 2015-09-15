package com.returnsoft.collection.exception;

import java.io.Serializable;

public class ServiceException extends Exception implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ServiceException() {
		super("Error al consultar servicio remoto");
	}
	
	protected ServiceException(String arg0) {
		super(arg0);
	}

	public ServiceException(Throwable arg0) {
		super(arg0);
	}
	
	public ServiceException(String arg0,Throwable arg1) {
		super(arg0,arg1);
	}
	

}
