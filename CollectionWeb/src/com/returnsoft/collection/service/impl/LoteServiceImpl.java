package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.BankEao;
import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.eao.CollectionPeriodEao;
import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.eao.ProductEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.eao.UserEao;
import com.returnsoft.collection.entity.Bank;
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.EaoException;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.LoteService;
import com.returnsoft.collection.util.CollectionFile;
import com.returnsoft.collection.util.SaleFile;

@Stateless
//@TransactionManagement(TransactionManagementType.BEAN)
public class LoteServiceImpl implements LoteService {

	//@Resource
	//private UserTransaction userTransaction;

	@EJB
	private LoteEao loteEao;

	@EJB
	private ProductEao productEao;

	@EJB
	private CollectionPeriodEao collectionPeriodEao;

	@EJB
	private UserEao userEao;

	@EJB
	private BankEao bankEao;
	
	
	@EJB
	private PayerEao payerEao;

	@EJB
	private SaleStateEao saleStateEao;

	@EJB
	private CreditCardEao creditCardEao;
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private CollectionEao collectionEao;
	
	//@EJB
	//private LoteService loteService;
	

	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	public void update(Lote lote) {
//
//		try {
//			
//			userTransaction.begin();
//			loteEao.update(lote);
//			userTransaction.commit();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

	//@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
//	public Lote create(String name, Integer total) throws ServiceException {
//
//		try {
//			
//			userTransaction.begin();
//
//			Lote lote = new Lote();
//			lote.setName(name);
//			lote.setTotal(total);
//			lote.setProcess(0);
//			lote.setDate(new Date());
//			lote.setState("En progreso");
//
//			loteEao.add(lote);
//			
//			userTransaction.commit();
//
//			return lote;			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
//				throw new ServiceException(e.getMessage(), e);
//			} else {
//				throw new ServiceException();
//			}
//		}
//
//	}

	public List<Lote> findByDate(Date date) throws ServiceException {
		try {

			return loteEao.findByDate(date);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage() != null && e.getMessage().trim().length() > 0) {
				throw new ServiceException(e.getMessage(), e);
			} else {
				throw new ServiceException();
			}
		}
	}

	@Asynchronous
	@Override
	public void addTypeSale(List<Sale> sales, String filename, SaleFile headers, Integer userId, Short bankId){
		
		Lote lote = new Lote();
		Date date = new Date();

		try {
			
			Bank bank = bankEao.findById(bankId);
			User user = userEao.findById(userId);

			lote.setName(filename);
			lote.setTotal(sales.size());
			lote.setProcess(0);
			lote.setDate(date);
			lote.setLoteType(LoteTypeEnum.CREATESALE);
			lote.setState("En progreso");
			loteEao.add(lote);
			// villanuevan@pe.geainternacional.com nidia
			

			Integer lineNumber = 1;

			for (Sale sale : sales) {
				sale.setBank(bank);
				sale.setCreatedBy(user);
				sale.setLote(lote);
				sale.setCreatedAt(date);
				saleEao.add(sale);
				
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
	
	
	@Asynchronous
	@Override
	public void addTypeCollection(List<Collection> collections, String filename, CollectionFile headers, Integer userId){
		
		Lote lote = new Lote();
		Date date = new Date();

		try {
			
			User user = userEao.findById(userId);

			lote.setName(filename);
			lote.setTotal(collections.size());
			lote.setProcess(0);
			lote.setDate(date);
			lote.setLoteType(LoteTypeEnum.CREATECOLLECTION);
			lote.setState("En progreso");
			loteEao.add(lote);

			Integer lineNumber = 1;

			for (Collection collection : collections) {
				
				collection.setCreatedBy(user);
				collection.setLote(lote);
				collection.setCreatedAt(date);
				collectionEao.add(collection);
				
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

}
