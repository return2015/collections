package com.returnsoft.collection.exception;

import java.io.Serializable;

public class SaleFileRowsInvalidException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1756340766183175607L;

	public SaleFileRowsInvalidException(Integer rows) {
		super("La cantidad de filas del archivo de ventas es inválida. Debería tener "+rows+" filas");
	}
	
	
	
}
