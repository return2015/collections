package com.returnsoft.collection.service;

import javax.ejb.Remote;

import com.returnsoft.collection.exception.ServiceException;


@Remote
public interface MailingService {
	
	public void mailerDaemon() throws ServiceException;

}
