package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataCreditCardDuplicateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2149546622827078075L;
	
	public DataCreditCardDuplicateException(int row1,int row2,String columnName, String saleCode) {
		super("Error en la fila "+row1+" y "+row2+" y columna "+columnName+". La venta con código "+saleCode+" está duplicada en el archivo.");
	}

}
