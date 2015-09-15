package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.AffiliationService;
@Stateless
public class AffiliationServiceImpl implements AffiliationService{
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private CommerceEao commerceEao;
	
	
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
	
	public Sale affiliate(String code, int userId, Date affiliationDate) throws ServiceException{
		try {
			
			Sale sale = saleEao.findByCode(code);
			sale.setAffiliation(true);
			sale.setAffiliationDate(affiliationDate);
			User user = new User();
			user.setId(userId);
			sale.setAffiliationUser(user);
			sale = saleEao.update(sale);
			
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

}
