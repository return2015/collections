package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationPendingException extends Exception implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2099189278785659524L;

	public NotificationPendingException(String saleCode) {
		super("La venta con código "+saleCode+" tiene notificaciones pendientes");
		
	}
	
}
