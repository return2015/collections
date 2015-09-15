package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataSaleDuplicateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -103080067517897678L;
	
	public DataSaleDuplicateException(int row1, int row2, String columnName1, String columnName2, String nuicInsured, String dateOfSale) {
		super("Error en la fila "+row1+" y "+row2+" y columnas "+columnName1+" y "+columnName2+". La venta con nuic de asegurado "+nuicInsured+" y fecha de venta "+dateOfSale+" está duplicado en el archivo.");
	}
	
	public DataSaleDuplicateException(int row, String columnName1, String columnName2, String nuicInsured, String dateOfSale) {
		super("Error en la fila "+row+" y columnas "+columnName1+" y "+columnName2+". La venta con nuic de asegurado "+nuicInsured+" y fecha de venta "+dateOfSale+" ya existe en la base de datos.");
	}

}
