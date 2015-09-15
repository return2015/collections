package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataColumnDecimalException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2871112788264402932L;
	
	public DataColumnDecimalException(int row, String columnName, int decimals) {
		super("Error en la fila "+row+" y columna "+columnName+". El valor tiene más de "+decimals+" decimales");
	}

}
