package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataRepaymentInsurancePremiumException  extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5893538251775966531L;
	
	public DataRepaymentInsurancePremiumException(int row, String columnName, String insurancePremiumNumber, Integer totalCollections) {
		super("Error en la fila "+row+" y columna "+columnName+" = "+insurancePremiumNumber+" La cantidad de primas debe ser menor o igual a la cantidad de cobranzas disponible = "+totalCollections);
	}

}
