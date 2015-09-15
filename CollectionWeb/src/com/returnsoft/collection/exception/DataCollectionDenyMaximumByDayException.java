package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;

public class DataCollectionDenyMaximumByDayException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076531389893729087L;
	
	public DataCollectionDenyMaximumByDayException(int row, String columnName1,String columnName2, String saleCode, String autorizationDate, int collectionsDenyByDay, int collections) {
		super("Error en la fila "+row+" y columnas "+columnName1+"="+saleCode+" y "+columnName2+"="+autorizationDate+". Máximo 2 cobranzas denegadas por día. Cobranzas denegadas por día="+collectionsDenyByDay+" y cobranzas denegadas a insertar="+collections);
	}

}
