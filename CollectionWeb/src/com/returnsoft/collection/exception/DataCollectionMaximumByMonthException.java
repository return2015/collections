package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class DataCollectionMaximumByMonthException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076531389893729087L;
	
	public DataCollectionMaximumByMonthException(int row, String columnName1,String columnName2, String saleCode, String autorizationDate, int collectionsByMonth, int collections) {
		super("Error en la fila "+row+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+autorizationDate+". M�ximo 4 cobranzas por mes. Cobranzas por mes="+collectionsByMonth+" y cobranzas a insertar="+collections);
	}

}
