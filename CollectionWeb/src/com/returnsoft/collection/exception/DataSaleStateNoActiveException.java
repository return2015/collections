package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.SaleStateEnum;

public class DataSaleStateNoActiveException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4108240226639845737L;
	
	public DataSaleStateNoActiveException(int row, String columnName, String saleCode) {
		super("Error en la fila "+row+" y columna "+columnName+". La venta con código "+saleCode+" no está en estado "+SaleStateEnum.ACTIVE.getName());
	}

}
