package com.returnsoft.collection.exception;

import java.io.Serializable;

public class BankNotSelectedException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5395041752968353183L;
	
	public BankNotSelectedException() {
		super("Debe seleccionar banco");
	}

}
