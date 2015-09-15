package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CreditCardService;


@Stateless
public class CreditCardServiceImpl  implements CreditCardService{
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private CommerceEao commerceEao;
	
	/*@EJB
	private SaleStateEao saleStateEao;*/
	
	@EJB
	private CreditCardEao creditCardUpdateEao;
	
	public List<Commerce> findCommercesByBankId(Short bankId) throws ServiceException{
		try {
			
			List<Commerce> commerces = commerceEao.findByBankId(bankId);
			return commerces;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public CreditCard add(CreditCard creditCard) throws ServiceException{
		try {
			
			creditCardUpdateEao.add(creditCard);
			
			Sale sale = saleEao.findById(creditCard.getSale().getId());
			System.out.println(sale.getId());
			sale.setCreditCard(creditCard);
			sale = saleEao.update(sale);
			
					
					/*Long creditCardNumber = sale.getCreditCardNumber();
					sale.setCreditCardNumber(creditCard.getNumber());
					creditCard.setNumber(creditCardNumber);
					
					Date creditCardExpirationDate = sale.getCreditCardExpirationDate();
					sale.setCreditCardExpirationDate(creditCard.getExpirationDate());
					creditCard.setExpirationDate(creditCardExpirationDate);
					
					String creditCardState = sale.getCreditCardState();
					sale.setCreditCardState(creditCard.getState());
					creditCard.setState(creditCardState);
					
					Integer creditCardDaysOfDefault = sale.getCreditCardDaysOfDefault();
					sale.setCreditCardDaysOfDefault(creditCard.getDaysOfDefault());
					creditCard.setDaysOfDefault(creditCardDaysOfDefault);*/
					
					
					
					
				
			return creditCard;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	public Sale findByCode(String code) throws ServiceException{
		try {
			
			Sale sale = saleEao.findByCode(code);
			
			return sale;
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	
	/*public List<SaleState> getSaleStates() throws ServiceException {
		try {

			List<SaleState> saleStates = saleStateEao.getSaleStates();

			return saleStates;

		} catch (EaoException e) {
			throw new ServiceException("EaoException:" + e.getMessage());
		} catch (Exception e) {
			throw new ServiceException("ServiceException:" + e.getMessage());
		}
	}*/

}
