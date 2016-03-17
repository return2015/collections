package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.CreditCardHistoryEao;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.CreditCardHistory;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CreditCardService;

@Stateless
public class CreditCardServiceImpl implements CreditCardService {

	/*
	 * @EJB private SaleEao saleEao;
	 */

	/*
	 * @EJB private CommerceEao commerceEao;
	 */

	@EJB
	private CreditCardEao creditCardEao;

	@EJB
	private CreditCardHistoryEao creditCardHistoryEao;

	// public CreditCard add(CreditCard creditCard) throws ServiceException{
	// try {
	//
	// creditCardUpdateEao.add(creditCard);
	//
	// Sale sale = saleEao.findById(creditCard.getSale().getId());
	// System.out.println(sale.getId());
	// sale.setCreditCard(creditCard);
	// sale = saleEao.update(sale);
	//
	// return creditCard;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
	// throw new ServiceException(e.getMessage(), e);
	// }else{
	// throw new ServiceException();
	// }
	// }
	// }
	
	@Override
	public CreditCard update(CreditCard creditCard) throws ServiceException {
		try {
			return creditCardEao.update(creditCard);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<CreditCardHistory> findBySale(Long saleId) throws ServiceException {
		try {
			return creditCardHistoryEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

}
