package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleStateService;

@Stateless
public class SaleStateServiceImpl implements SaleStateService {

	@EJB
	private SaleEao saleEao;


	@EJB
	private CommerceEao commerceEao;

	@EJB
	private SaleStateEao saleStateEao;

	
	public void add(SaleState maintenance) throws ServiceException {
		try {
			
			saleStateEao.add(maintenance);

			Sale sale = saleEao.findById(maintenance.getSale().getId());
			
			sale.setSaleState(maintenance);
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
	
	
	public List<SaleState> findBySale(Long saleId) throws ServiceException{
		try {
			
			List<SaleState> maintenances = saleStateEao.findBySaleId(saleId);

			return maintenances;
			
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
