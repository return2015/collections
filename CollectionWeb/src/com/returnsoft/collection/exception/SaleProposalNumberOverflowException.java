package com.returnsoft.collection.exception;

public class SaleProposalNumberOverflowException extends Exception{

	public SaleProposalNumberOverflowException(int length) {
		super("El n�mero de propuesta debe tener "+length+" d�gitos.");
	}

}
