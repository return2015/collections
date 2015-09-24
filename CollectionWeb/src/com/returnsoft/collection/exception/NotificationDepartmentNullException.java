package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationDepartmentNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4168707542148908299L;

	
	public NotificationDepartmentNullException() {
		super("El departamento esta vacío.");
	}
	
}
