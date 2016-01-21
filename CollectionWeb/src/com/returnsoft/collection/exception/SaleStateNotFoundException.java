package com.returnsoft.collection.exception;

public class SaleStateNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4625743307575647118L;

	public SaleStateNotFoundException() {
		super("No se encontró estado de venta");
	}
	
	public SaleStateNotFoundException(String columnName, int row) {
		super("Error en la fila " + row + " y columna " + columnName + ": No se encontró estado de venta");
	}
	
	

}
