package com.returnsoft.collection.exception;

import java.io.Serializable;

public class CommerceCodeException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6412943846546540114L;
	
	public CommerceCodeException(String saleCode, String commerceCode) {
		super("C�digo de comercio "+commerceCode+" inv�lido en la venta con c�digo "+saleCode);
	}

}
