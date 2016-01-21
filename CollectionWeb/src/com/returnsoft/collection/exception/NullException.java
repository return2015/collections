package com.returnsoft.collection.exception;

public class NullException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4751993132948135539L;

	public NullException(String columnName, int row) {
		super("Error en la fila " + row + " y columna " + columnName + ": No puede estar vacío.");
	}
	
	public NullException(String attributeName, String saleCode) {
		super("Error en la venta "+saleCode+" El atributo " + attributeName + ": No puede estar vacío.");
	}
	
	

}
