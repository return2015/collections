package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataMaintenanceDuplicateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 424761024438106122L;
	
	public DataMaintenanceDuplicateException(int row1,int row2,String columnName, String saleCode) {
		super("Error en la fila "+row1+" y "+row2+" y columna "+columnName+". La venta con código "+saleCode+" está duplicada en el archivo.");
	}

}
