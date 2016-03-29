package com.returnsoft.collection.exception;

import java.io.Serializable;

public class FileRowsInvalidException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1756340766183175607L;

	public FileRowsInvalidException(Integer lineNumber, Integer rows) {
		super("Error en la fila "+lineNumber+".La cantidad de filas del archivo es inválido. Debería tener "+rows+" columnas");
	}
	
	
	
}
