package com.returnsoft.collection.exception;

public class BankNotFoundException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2275607638238791442L;
	
	
	public BankNotFoundException(String columnName, int row) {
		super("Error en la fila " + row + " y columna " + columnName + ": Banco no encontrado.");
	}

}
