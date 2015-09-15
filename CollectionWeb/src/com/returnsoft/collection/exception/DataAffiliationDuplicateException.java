package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataAffiliationDuplicateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5783314280624250175L;
	
	public DataAffiliationDuplicateException(int row1,int row2, String columnName, String saleCode) {
		super("Error en la fila "+row1+" y "+row2+" y columna "+columnName+". La venta con código "+saleCode+" está duplicada en el archivo.");
	}
	
	public DataAffiliationDuplicateException(int row, String columnName, String saleCode) {
		super("Error en la fila "+row+" y columna "+columnName+". La venta con código "+saleCode+" ya está afiliada en la base de datos.");
	}
	

}
