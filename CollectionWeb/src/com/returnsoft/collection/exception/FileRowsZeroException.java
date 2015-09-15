package com.returnsoft.collection.exception;

import java.io.Serializable;

public class FileRowsZeroException  extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6831202138951866305L;
	
	public FileRowsZeroException() {
		super("No se encontraron filas con datos.");
	}

}
