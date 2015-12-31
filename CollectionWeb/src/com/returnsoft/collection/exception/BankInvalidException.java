package com.returnsoft.collection.exception;

public class BankInvalidException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8861333525434924314L;

	public BankInvalidException() {
		super("Banco inválido.");
	}
	
	public BankInvalidException(String columnName, int row) {
		super("Error en la fila " + row + " y columna " + columnName + ": Banco inválido.");
	}
}
