package com.returnsoft.collection.exception;

import java.io.Serializable;

public class SaleBankInvalidException extends Exception implements Serializable{

	public SaleBankInvalidException() {
		super("Banco inválido");
	}

}
