package com.returnsoft.collection.exception;

public class SaleNuicContractorOverFlowException extends Exception {

	public SaleNuicContractorOverFlowException(int length) {
		super("El nuic de contratante debe tener "+length+" dígitos.");
	}

}
