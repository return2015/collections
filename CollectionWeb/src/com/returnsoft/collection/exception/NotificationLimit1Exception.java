package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationLimit1Exception extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3987736602821534963L;
	
	public NotificationLimit1Exception(String saleCode) {
		super("La venta con c�digo "+saleCode+" ya tiene 1 notificaci�n f�sica y 3 virtuales");
		
	}

}
