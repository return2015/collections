package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataSaleNotFoundException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6535782280582461996L;
	
	public DataSaleNotFoundException(int row, String columnName, String saleCode) {
		super("Error en la fila "+row+" y columna "+columnName+". No se encontró la venta con código "+saleCode);
	}

}
