package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataColumnNumberException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4515879505468438559L;
	
	public DataColumnNumberException(int row, String columnName) {
		super("Error en la fila "+row+" y columna "+columnName+". El valor tiene que ser numérico.");
	}


}
