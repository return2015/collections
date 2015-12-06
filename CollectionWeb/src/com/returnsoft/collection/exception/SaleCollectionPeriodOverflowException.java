package com.returnsoft.collection.exception;

public class SaleCollectionPeriodOverflowException extends Exception {

	public SaleCollectionPeriodOverflowException(int length) {
		super("El periodo de cobro debe tener "+length+" dígitos.");
	}

}
