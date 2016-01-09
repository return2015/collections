package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Collection;
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

			/*Sale sale = saleEao.findByCode(collection.getSaleCode());
			collection.setSale(sale);*/
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


	public Integer findByResponseAndAuthorizationDay(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode)
			throws ServiceException {
		try {

			return collectionEao
					.findByResponseAndAuthorizationDay(messageResponse,authorizationDate,saleCode);

			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}

	}
	
	public Integer findByResponseAndAuthorizationMonth(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode)
			throws ServiceException {
		try {

			return collectionEao
					.findByResponseAndAuthorizationMonth(messageResponse,authorizationDate,saleCode);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}

	}
	
	
	public List<Collection> findAllowsBySale(Long saleId) throws ServiceException{
		try {

			List<Collection> collections = collectionEao.findAllowsBySaleId(saleId);

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
	
	public List<Collection> findBySale(Long saleId) throws ServiceException{
		try {
			
			List<Collection> collections = collectionEao.findBySaleId(saleId);

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
