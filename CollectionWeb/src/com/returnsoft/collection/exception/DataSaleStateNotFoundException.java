package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.SaleStateEnum;

public class DataSaleStateNotFoundException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3253478627650924335L;
	
	public DataSaleStateNotFoundException(int row, String columnName, String saleState) {
		super("Error en la fila "+row+" y columna "+columnName+". El estado de la venta es "+saleState+". Y debe ser "+SaleStateEnum.ACTIVE.getName() +" ó " + SaleStateEnum.DOWN.getName());
	}

}
