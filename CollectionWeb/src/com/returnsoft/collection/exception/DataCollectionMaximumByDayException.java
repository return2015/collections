package com.returnsoft.collection.exception;

import java.io.Serializable;


public class DataCollectionMaximumByDayException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076531389893729087L;
	
	public DataCollectionMaximumByDayException(int row, String columnName1,String columnName2, String saleCode, String autorizationDate, int collectionsByDay, int collections) {
		super("Error en la fila "+row+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+autorizationDate+". M�ximo 2 cobranzas por d�a. Cobranzas por d�a="+collectionsByDay+" y cobranzas a insertar="+collections);
	}

}
