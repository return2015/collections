package com.returnsoft.collection.exception;

public class DecimalException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5582844198217133331L;
	

	public DecimalException(int row, String columnName,  int decimal) {
		super("Error en la fila " + row + " y columna " + columnName + ": La máxima cantidad de decimales es: "+decimal);
	}

}
