package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataSalePaymentMethodException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1117275704239894770L;
	
	public DataSalePaymentMethodException(int row, String columnName, String paymentMethodException) {
		super("Error en la fila "+row+" y columna "+columnName+". Medio de pago "+paymentMethodException+" inválido");
	}

}
