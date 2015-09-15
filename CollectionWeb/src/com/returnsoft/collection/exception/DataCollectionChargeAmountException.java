package com.returnsoft.collection.exception;

import java.io.Serializable;
import java.math.BigDecimal;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class DataCollectionChargeAmountException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2368537443809601830L;
	
	public DataCollectionChargeAmountException(int row, String columnName, String responseMessage) {
		super("Error en la fila "+row+" y columna "+columnName+". Debe ser cero cuando "+responseMessage+ " es "+CollectionResponseEnum.DENY.getName());
	}
	
	public DataCollectionChargeAmountException(int row, String columnName, BigDecimal insurancePremium, BigDecimal chargeAmount) {
		super("Error en la fila "+row+" y columna "+columnName+" = "+chargeAmount+" debe ser igual a la prima de la venta="+insurancePremium);
	}
	

}
