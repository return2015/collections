package com.returnsoft.collection.exception;

import java.io.Serializable;

public class SaleNotFoundException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8939404459734672314L;
	
	public SaleNotFoundException() {
		super("No se encontró la venta");
	}
	
	public SaleNotFoundException(String columnName, int row) {
		super("Error en la fila " + row + " y columna " + columnName + ": No se encontró venta");
	}

}
