package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationDistrictNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5956890254048700836L;

	
	public NotificationDistrictNullException() {
		super("El distrito esta vacío.");
	}
	
}
