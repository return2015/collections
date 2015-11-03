package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.RepaymentEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.RepaymentService;

@Stateless
public class RepaymentServiceImpl implements RepaymentService{
	
	@EJB
	private CommerceEao commerceEao;
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private CollectionEao collectionEao;
	
	@EJB
	private RepaymentEao repaymentEao;
	
	
	public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws ServiceException{
		try {

			Repayment repayment = repaymentEao.findBySaleIdAndReturnedDate(saleId, returnedDate);

			return repayment;

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
		
	}
	
	
	public void add(Repayment repayment) throws ServiceException {
		try {

			Sale sale = saleEao.findByCode(repayment.getCode());
			repayment.setSale(sale);
			
			/*Collection collection = collectionEao.findByReceiptNumber(repayment.getReceiptNumber());
			repayment.setCollection(collection);*/
			
			repaymentEao.add(repayment);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}
	
	public List<Repayment> findBySale(Long saleId) throws ServiceException{
		try {
			
			List<Repayment> repayments = repaymentEao.findBySaleId(saleId);

			return repayments;
			
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
