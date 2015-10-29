package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationLimit3Exception extends Exception implements Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2843570782585578695L;

	public NotificationLimit3Exception(String saleCode) {
		super("La venta con código "+saleCode+" ya tiene 3 notificaciones virtuales");
		
	}

}
