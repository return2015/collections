package com.returnsoft.collection.exception;

public class SaleInsurancePremiumOverflowException extends Exception {

	public SaleInsurancePremiumOverflowException(int lenght) {
		super("La prima debe tener "+lenght+" dígitos.");

	}

}
