package com.returnsoft.collection.exception;

import java.io.Serializable;

public class BankLetterNotFoundException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7630864818243795586L;
	

	public BankLetterNotFoundException(String saleCode, String bankName) {
		super("No se encontró formato de carta disponible para la venta con código "+saleCode+" y banco "+bankName+".");
	}

}
