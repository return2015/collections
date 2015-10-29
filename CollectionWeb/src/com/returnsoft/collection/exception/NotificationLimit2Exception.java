package com.returnsoft.collection.exception;

import java.io.Serializable;

public class NotificationLimit2Exception extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8399003848433735013L;
	
	public NotificationLimit2Exception(String saleCode) {
		super("La venta con c�digo "+saleCode+" ya tiene 2 notificaciones f�sicas y menos de 3 virtuales");
		
	}

}
