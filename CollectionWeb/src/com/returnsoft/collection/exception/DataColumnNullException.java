package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataColumnNullException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 743050646386222444L;
	
	public DataColumnNullException(int row, String columnName) {
		super("Error en la fila "+row+" y columna "+columnName+". El valor no puede ser nulo.");
	}

}
