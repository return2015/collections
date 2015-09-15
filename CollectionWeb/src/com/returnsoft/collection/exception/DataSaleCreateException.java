package com.returnsoft.collection.exception;

import java.io.Serializable;

public class DataSaleCreateException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8684325909469288827L;
	
	public DataSaleCreateException(int row, String message) {
		super("Error en la fila "+row+". No se creó la venta. "+message);
	}

}
