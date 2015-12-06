package com.returnsoft.collection.exception;

import java.io.Serializable;

public class SaleProductInvalidException  extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5757390614374078771L;
	
	public SaleProductInvalidException() {
		super("Producto inválido");
	}

}
