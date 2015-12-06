package com.returnsoft.collection.exception;

public class SaleLastnameMaternalContractorOverFlowException extends Exception {

	public SaleLastnameMaternalContractorOverFlowException(int length) {
		super("El apellido amterno de contratante debe tener "+length+" dígitos.");
	}

}
