package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.BankService;

@Stateless
public class BankServiceImpl implements BankService{
	
	@EJB
	private BankEao bankEao;
	
	@Override
	public List<Bank> getAll() throws ServiceException{
		try {
			return bankEao.getBanks();
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}
	
	@Override
	public Bank findById(Short bankId) throws ServiceException {
		try {
			return bankEao.findById(bankId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}
	
	@Override
	public List<Bank> findByUser(Integer userId) throws ServiceException {
		try {
			return bankEao.findByUserId(userId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}
	

}
