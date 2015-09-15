package com.returnsoft.collection.exception;

import java.io.Serializable;

public class FileNotFoundException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4501096319743171766L;
	
	public FileNotFoundException() {
		super("Debe ingresar archivo .csv");
	}

}
