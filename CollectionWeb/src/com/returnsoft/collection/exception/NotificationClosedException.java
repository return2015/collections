package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationClosedException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2149773880022485709L;
	
	public NotificationClosedException(String saleCode) {
		super("La venta con código "+saleCode+" tiene notificaciones cerradas");
		
	}
	

}
