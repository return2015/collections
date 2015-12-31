package com.returnsoft.collection.exception;

public class OverflowException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5674505814875666940L;

	public OverflowException(String columnName, int row, int length) {
		super("Error en la fila " + row + " y columna " + columnName + ": Debe tener "+length+" caracteres.");
	}
	
	

}
