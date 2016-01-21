package com.returnsoft.collection.exception;

public class FormatException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9180754155009925086L;

	public FormatException(String columnName, int row) {
		super("Error en la fila " + row + " y columna " + columnName + ": Tipo de dato inválido.");
	}

}
