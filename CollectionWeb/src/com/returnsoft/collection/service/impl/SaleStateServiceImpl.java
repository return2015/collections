package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.eao.SaleStateHistoryEao;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleStateService;

@Stateless
public class SaleStateServiceImpl implements SaleStateService {

	//@EJB
	//private SaleEao saleEao;


	//@EJB
	//private CommerceEao commerceEao;

	@EJB
	private SaleStateEao saleStateEao;
	
	@EJB
	private SaleStateHistoryEao saleStateHistoryEao;

	
	/*public void add(SaleState maintenance) throws ServiceException {
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
	}*/
	
	public SaleState update(SaleState saleState) throws ServiceException {
	try {
		
		return saleStateEao.update(saleState);

	} catch (Exception e) {
		e.printStackTrace();
		if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
			throw new ServiceException(e.getMessage(), e);	
		}else{
			throw new ServiceException();
		}
	}
}
	
	
	public List<SaleStateHistory> findBySale(Long saleId) throws ServiceException{
		try {
			
			List<SaleStateHistory> saleStates = saleStateHistoryEao.findBySaleId(saleId);

			return saleStates;
			
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
