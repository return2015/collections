package com.returnsoft.collection.exception;

import java.io.Serializable;

public class SalesNotFoundException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8939404459734672314L;
	
	public SalesNotFoundException() {
		super("No se encontraron ventas");
	}

}
