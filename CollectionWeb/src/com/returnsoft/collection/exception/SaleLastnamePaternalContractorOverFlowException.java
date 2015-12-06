package com.returnsoft.collection.exception;

public class SaleLastnamePaternalContractorOverFlowException extends Exception {

	public SaleLastnamePaternalContractorOverFlowException(int length) {
		super("El apellido paterno de contratante debe tener "+length+" dígitos.");
	}

}
