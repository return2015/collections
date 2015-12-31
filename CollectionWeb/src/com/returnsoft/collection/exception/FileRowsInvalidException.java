package com.returnsoft.collection.exception;

import java.io.Serializable;

public class FileRowsInvalidException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1756340766183175607L;

	public FileRowsInvalidException(Integer rows) {
		super("La cantidad de filas del archivo es inválido. Debería tener "+rows+" filas");
	}
	
	
	
}
