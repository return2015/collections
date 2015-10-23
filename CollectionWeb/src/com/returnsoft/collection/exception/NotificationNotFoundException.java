package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationNotFoundException extends Exception implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7138055922077551139L;

	public NotificationNotFoundException(String saleCode) {
		super("La venta con código "+saleCode+" no tiene notificaciones");
		
	}

}
