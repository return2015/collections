package com.returnsoft.collection.exception;

public class SaleAlreadyExistException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3849479521649884167L;

	public SaleAlreadyExistException() {
		super("La venta ya existe");
	}
	
	public SaleAlreadyExistException(int row) {
		super("Error en la fila " + row + ": La venta ya existe");
	}
	
	

}
