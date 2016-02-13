package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.eao.SaleStateHistoryEao;
import com.returnsoft.collection.eao.UserEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.EaoException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleStateService;
import com.returnsoft.collection.util.SaleStateFile;

@Stateless
public class SaleStateServiceImpl implements SaleStateService {

	//@EJB
	//private SaleEao saleEao;


	//@EJB
	//private CommerceEao commerceEao;
	
	@EJB
	private UserEao userEao;
	
	@EJB
	private LoteEao loteEao;

	@EJB
	private SaleStateEao saleStateEao;
	
	@EJB
	private SaleStateHistoryEao saleStateHistoryEao;
	
	
	@Asynchronous
	@Override
	public void updateSaleStateList(List<SaleState> saleStates, String filename, SaleStateFile headers, Integer userId, Short bankId){
		
		Lote lote = new Lote();
		Date date = new Date();

		try {
			
			//Bank bank = bankEao.findById(bankId);
			User user = userEao.findById(userId);

			lote.setName(filename);
			lote.setTotal(saleStates.size());
			lote.setProcess(0);
			lote.setCreatedAt(date);
			lote.setLoteType(LoteTypeEnum.UPDATESALESTATE);
			lote.setState("En progreso");
			loteEao.add(lote);
			// villanuevan@pe.geainternacional.com nidia
			

			Integer lineNumber = 1;

			for (SaleState saleState : saleStates) {
				
				saleState.setUpdatedBy(user);
				saleState.setLote(lote);
				saleState.setUpdatedAt(date);
				saleStateEao.update(saleState);
				
				lote.setProcess(lineNumber);
				loteEao.update(lote);
				
				lineNumber++;
			}

			lote.setState("Terminado");
			loteEao.update(lote);
			
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage()!=null || e.getMessage().length()==0) {
				lote.setState((new NullPointerException()).toString());
			}else{
				if (e.getMessage().length()>500) {
					lote.setState(e.getMessage().substring(0,500));	
				}else{
					lote.setState(e.getMessage());
				}
			}
			try {
				loteEao.update(lote);
			} catch (EaoException e1) {
				e1.printStackTrace();
			}

		}

	}
	

	
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
