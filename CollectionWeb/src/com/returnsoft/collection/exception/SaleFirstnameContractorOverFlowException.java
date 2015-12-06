package com.returnsoft.collection.exception;

public class SaleFirstnameContractorOverFlowException extends Exception{

	public SaleFirstnameContractorOverFlowException(int length) {
		super("El nombre de contratante debe tener "+length+" dígitos.");
	}

}
