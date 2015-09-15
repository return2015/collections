package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataRepaymentDuplicateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3677527365823670363L;
	
	public DataRepaymentDuplicateException(int row1,int row2,String columnName1,String columnName2, String saleCode, String returnedDate) {
		super("Error en la fila "+row1+" y "+row2+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+returnedDate+". El extorno está duplicado.");
	}
	
	public DataRepaymentDuplicateException(int row1,String columnName1,String columnName2, String saleCode, String returnedDate) {
		super("Error en la fila "+row1+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+returnedDate+". El extorno ya está registrado.");
	}

}
