package com.returnsoft.collection.exception;

import java.io.Serializable;

import com.returnsoft.collection.enumeration.SaleStateEnum;

public class SaleStateNoActiveException extends Exception implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8911391086944159614L;
	
	public SaleStateNoActiveException(String saleCode) {
		super("La venta con código único "+saleCode+" no esta "+SaleStateEnum.ACTIVE.getName());
	}

}
