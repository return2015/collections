package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataCollectionCreateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6751487317154802285L;
	
	public DataCollectionCreateException(int row, String message) {
		super("Error en la fila "+row+". No se creó la cobranza. "+message);
	}

}
