package com.returnsoft.collection.exception;

public class PayerNotFoundException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8696371285078272904L;

	public PayerNotFoundException() {
		super("No se encontró responsable de pago.");
	}
	
	

}
