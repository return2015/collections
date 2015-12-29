package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.CreditCardHistoryEao;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.CreditCardHistory;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CreditCardService;


@Stateless
public class CreditCardServiceImpl  implements CreditCardService{
	
	/*@EJB
	private SaleEao saleEao;*/
	
	/*@EJB
	private CommerceEao commerceEao;*/
		
	@EJB
	private CreditCardEao creditCardEao;
	
	@EJB
	private CreditCardHistoryEao creditCardHistoryEao;
	
	
	/*public CreditCard add(CreditCard creditCard) throws ServiceException{
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
	}*/
	
	public CreditCard update(CreditCard creditCard) throws ServiceException{
	try {
		
		return creditCardEao.update(creditCard);
		
		

	} catch (Exception e) {
		e.printStackTrace();
		if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
			throw new ServiceException(e.getMessage(), e);	
		}else{
			throw new ServiceException();
		}
	}
}
	
	
		
	public List<CreditCardHistory> findBySale(Long saleId) throws ServiceException{
		try {
			
			List<CreditCardHistory> creditCardUpdates = creditCardHistoryEao.findBySaleId(saleId);

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
