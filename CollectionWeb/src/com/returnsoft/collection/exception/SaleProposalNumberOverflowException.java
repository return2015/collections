package com.returnsoft.collection.exception;

public class SaleProposalNumberOverflowException extends Exception{

	public SaleProposalNumberOverflowException(int length) {
		super("El número de propuesta debe tener "+length+" dígitos.");
	}

}
