package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class DataCollectionAllowMaximumByDayException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076531389893729087L;
	
	public DataCollectionAllowMaximumByDayException(int row, String columnName1,String columnName2, String saleCode, String autorizationDate, int collectionsAllowByDay, int collections) {
		super("Error en la fila "+row+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+autorizationDate+". Máximo 2 cobranzas aprobadas por día. Cobranzas aprobadas por día="+collectionsAllowByDay+" y cobranzas aprobadas a insertar="+collections);
	}

}
