package com.returnsoft.collection.eao;

import java.util.List;

import com.returnsoft.collection.entity.Notification;
import com.returnsoft.collection.exception.EaoException;


public interface MailingEao {
	
	public List<Notification> findBySaleId(Long saleId) throws EaoException;

}
