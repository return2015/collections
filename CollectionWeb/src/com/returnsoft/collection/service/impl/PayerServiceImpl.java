package com.returnsoft.collection.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.PayerService;
@Stateless
public class PayerServiceImpl implements PayerService{
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private PayerEao payerEao;
	
	
	public void add(Payer payer) throws ServiceException {
		try {
			
			payerEao.add(payer);

			Sale sale = saleEao.findById(payer.getSale().getId());
			sale.setPayer(payer);
			sale = saleEao.update(sale);


		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public Sale findSaleById(Long id) throws ServiceException {
		try {

			Sale sale = saleEao.findById(id);

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
