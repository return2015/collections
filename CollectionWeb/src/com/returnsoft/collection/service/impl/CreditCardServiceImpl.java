package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.SaleEao;
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
		
	@EJB
	private CreditCardEao creditCardUpdateEao;
	
	
	public CreditCard add(CreditCard creditCard) throws ServiceException{
		try {
			
			creditCardUpdateEao.add(creditCard);
			
			Sale sale = saleEao.findById(creditCard.getSale().getId());
			System.out.println(sale.getId());
			sale.setCreditCard(creditCard);
			sale = saleEao.update(sale);
			
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
	
		
	public List<CreditCard> findBySale(Long saleId) throws ServiceException{
		try {
			
			List<CreditCard> creditCardUpdates = creditCardUpdateEao.findBySaleId(saleId);

			return creditCardUpdates;
			
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
