package com.returnsoft.collection.exception;

import java.io.Serializable;

public class EaoException extends Exception implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public EaoException() {
		super();
	}

	public EaoException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public EaoException(String arg0) {
		super(arg0);
	}

	public EaoException(Throwable arg0) {
		super(arg0);
	}	

}
