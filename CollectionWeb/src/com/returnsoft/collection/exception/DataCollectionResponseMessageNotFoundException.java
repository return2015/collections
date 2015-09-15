package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.CollectionResponseEnum;


public class DataCollectionResponseMessageNotFoundException  extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3988760051658501122L;
	
	public DataCollectionResponseMessageNotFoundException(int row, String columnName, String responseMessage) {
		super("Error en la fila "+row+" y columna "+columnName+". El mensaje de respuesta es "+responseMessage+". Y debe ser "+CollectionResponseEnum.ALLOW.getName() +" ó " + CollectionResponseEnum.DENY.getName());
	}

}
