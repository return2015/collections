package com.returnsoft.collection.exception;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class CollectionChargeAmountException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1198608343662629632L;

	public CollectionChargeAmountException(String columnName, int row) {
		super("Error en la fila " + row + " y columna " + columnName + ": Debe ser cero cuando el mensaje de respuesta es "+CollectionResponseEnum.DENY.getName());
	}
	
	
	
	

}
