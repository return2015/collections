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
	
	public List<Bank> getAll() throws ServiceException{
		try {
			
			return bankEao.getBanks();
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public Bank findById(Short bankId) throws ServiceException {
		try {

			Bank bank = bankEao.findById(bankId);

			return bank;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public List<Bank> findByUser(Integer userId) throws ServiceException {
		try {

			List<Bank> banks = bankEao.findByUserId(userId);

			return banks;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	

}
