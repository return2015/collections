package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class DataCollectionAllowMaximumByMonthException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076531389893729087L;
	
	public DataCollectionAllowMaximumByMonthException(int row, String columnName1,String columnName2, String saleCode, String autorizationDate, int collectionsAllowByMonth, int collections) {
		super("Error en la fila "+row+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+autorizationDate+". Máximo 2 cobranzas aprobadas por mes. Cobranzas aprobadas por mes="+collectionsAllowByMonth+" y cobranzas aprobadas a insertar="+collections);
	}

}
