package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationProvinceNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8410463710176440846L;
	
	public NotificationProvinceNullException() {
		super("El provincia esta vacío.");
	}

}
