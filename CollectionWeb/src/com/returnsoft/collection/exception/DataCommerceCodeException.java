package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataCommerceCodeException extends Exception implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -817680826208746869L;

	public DataCommerceCodeException(int row, String columnName, String commerceCodeException) {
		super("Error en la fila "+row+" y columna "+columnName+". C�digo de comercio "+commerceCodeException+" inv�lido");
	}

}
