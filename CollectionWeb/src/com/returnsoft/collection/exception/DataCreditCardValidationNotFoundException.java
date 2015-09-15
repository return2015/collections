package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.CreditCardValidationEnum;

public class DataCreditCardValidationNotFoundException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6228801003490176414L;
	
	public DataCreditCardValidationNotFoundException(int row, String columnName, String validation) {
		super("Error en la fila "+row+" y columna "+columnName+". El validaci�n es "+validation+". Y debe ser "+CreditCardValidationEnum.NOTFOUND.getName() +" � " + CreditCardValidationEnum.SAME.getName()+" � " + CreditCardValidationEnum.UPDATE.getName());
	}

}
