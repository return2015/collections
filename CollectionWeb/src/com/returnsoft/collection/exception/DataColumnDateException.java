package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataColumnDateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6350183015645848530L;
	
	public DataColumnDateException(int row, String columnName, String format) {
		super("Error en la fila "+row+" y columna "+columnName+". El valor tiene que tener formato "+format);
	}

}
