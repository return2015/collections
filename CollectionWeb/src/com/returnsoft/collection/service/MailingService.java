package com.returnsoft.collection.service;

import javax.ejb.Remote;

import com.returnsoft.collection.exception.ServiceException;


//@Remote
public interface MailingService {
	
	public void mailerDaemon() throws ServiceException;
	
	public void sendMail(String email, String names, Short bankId) throws ServiceException;

}
