package com.returnsoft.collection.service.impl;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.CommerceEao;
import com.returnsoft.collection.eao.MaintenanceEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Commerce;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.MaintenanceService;

@Stateless
public class MaintenanceServiceImpl implements MaintenanceService {

	@EJB
	private SaleEao saleEao;


	@EJB
	private CommerceEao commerceEao;

	@EJB
	private MaintenanceEao maintenanceEao;

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
	
	public Sale findSaleByCode(String code) throws ServiceException {
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

	

	public void add(SaleState maintenance) throws ServiceException {
		try {
			
			maintenanceEao.add(maintenance);

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
	
	

}
