package com.returnsoft.collection.exception;

import java.io.Serializable;

public class FileColumnsTotalException extends Exception implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6158317971079719683L;

	public FileColumnsTotalException(int row, int totalColumnsException, int totalColumns) {
		super("Error en la fila: "+row+". Se encontraron "+totalColumnsException+" columnas pero debería ser "+totalColumns+"");
	}

}
