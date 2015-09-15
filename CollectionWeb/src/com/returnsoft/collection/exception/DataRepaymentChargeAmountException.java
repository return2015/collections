package com.returnsoft.collection.exception;

import java.io.Serializable;
import java.math.BigDecimal;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class DataRepaymentChargeAmountException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2368537443809601830L;
	
	public DataRepaymentChargeAmountException(int row, String columnName, String chargeAmount, BigDecimal insurancePremium) {
		super("Error en la fila "+row+" y columna "+columnName+" = "+chargeAmount+" debe ser igual a la prima de la venta="+insurancePremium);
	}
	

}
