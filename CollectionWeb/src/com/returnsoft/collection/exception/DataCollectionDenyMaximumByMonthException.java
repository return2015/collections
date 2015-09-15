package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class DataCollectionDenyMaximumByMonthException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076531389893729087L;
	
	public DataCollectionDenyMaximumByMonthException(int row, String columnName1,String columnName2, String saleCode, String autorizationDate, int collectionsDenyByMonth, int collections) {
		super("Error en la fila "+row+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+autorizationDate+". Máximo 4 cobranzas denegadas por mes. Cobranzas denegadas por mes="+collectionsDenyByMonth+" y cobranzas denegadas a insertar="+collections);
	}

}
