package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataColumnLengthException extends Exception implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6980216755093983301L;

	public DataColumnLengthException(int row, String columnName, int lengthException, int lenght) {
		super("Error en la fila "+row+" y columna "+columnName+". Tamaño del valor "+lengthException+". Debería ser "+lenght);
	}

}
