package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.CollectionResponseEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CollectionService;

@Stateless
public class CollectionServiceImpl implements CollectionService {

	@EJB
	private SaleEao saleEao;

	@EJB
	private CommerceEao commerceEao;

	@EJB
	private CollectionEao collectionEao;

	public void add(Collection collection) throws ServiceException {
		try {

			Sale sale = saleEao.findByCode(collection.getSaleCode());
			collection.setSale(sale);
			collectionEao.add(collection);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}

	public List<Commerce> findCommercesByBankId(Short bankId)
			throws ServiceException {
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

	public Sale findByCode(String code) throws ServiceException {
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

	public List<Collection> findByResponseAndAuthorizationDay(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode)
			throws ServiceException {
		try {

			List<Collection> collections = collectionEao
					.findByResponseAndAuthorizationDay(messageResponse,authorizationDate,saleCode);

			return collections;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}

	}
	
	public List<Collection> findByResponseAndAuthorizationMonth(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode)
			throws ServiceException {
		try {

			List<Collection> collections = collectionEao
					.findByResponseAndAuthorizationMonth(messageResponse,authorizationDate,saleCode);

			return collections;

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
