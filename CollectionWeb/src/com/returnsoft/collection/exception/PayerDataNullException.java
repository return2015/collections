package com.returnsoft.collection.exception;

import java.io.Serializable;

public class PayerDataNullException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1418124518959096681L;
	
	public PayerDataNullException(String nameType,String saleCode) {
		super(nameType+" de la venta "+saleCode+" esta vacío.");
	}

}
