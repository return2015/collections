package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataSaleProductException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2884803847977513311L;
	
	public DataSaleProductException(int row, String columnName, String productException) {
		super("Error en la fila "+row+" y columna "+columnName+". Producto "+productException+" inválido");
	}


}
