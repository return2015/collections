package com.returnsoft.collection.exception;

import java.io.Serializable;

public class FileExtensionException extends Exception implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2952477298190569425L;
	
	public FileExtensionException() {
		super("Solo se admiten archivos con extensión .csv");
	}

}
