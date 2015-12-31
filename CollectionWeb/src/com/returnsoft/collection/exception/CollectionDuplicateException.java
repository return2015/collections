package com.returnsoft.collection.exception;

public class CollectionDuplicateException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3178447917311821494L;
	
	/*public SaleDuplicateException() {
		super("La venta está duplicada.");
	}*/
	
	public CollectionDuplicateException(int row) {
		super("Error en la fila " + row + ": La cobranza está duplicada.");
	}
	

}
