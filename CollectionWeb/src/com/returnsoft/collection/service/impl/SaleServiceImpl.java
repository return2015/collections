package com.returnsoft.collection.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.transaction.UserTransaction;

import com.returnsoft.collection.eao.CreditCardHistoryEao;
import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleService;
import com.returnsoft.collection.vo.SaleFile;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SaleServiceImpl implements SaleService {

	@EJB
	private SaleEao saleEao;
	
	@EJB
	private CreditCardHistoryEao creditCardHistoryEao;

	@EJB
	private LoteEao loteEao;

	@Resource
	private EJBContext context;

	@Override
	public Sale findById(Long saleId) throws ServiceException {
		try {
			return saleEao.findById(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Sale findByCode(String code){
		try {
			return saleEao.findByCode(code);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Asynchronous
	@Override
	public void addSaleList(List<Sale> sales, SaleFile headers, String filename, User createdBy) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();

		try {

			Lote lote = new Lote(filename, sales.size(), "En progreso", LoteTypeEnum.CREATESALE, createdBy, current);
			loteEao.add(lote);

			for (Sale sale : sales) {

				try {

					utx.begin();

					sale.setLote(lote);
					sale.setCreatedBy(createdBy);
					sale.setCreatedAt(current);
					saleEao.add(sale);

					lote.setProcess(process);
					loteEao.update(lote);

					utx.commit();

				} catch (Exception e) {
					e.printStackTrace();

					try {
						utx.rollback();
					} catch (Exception e2) {
						e2.printStackTrace();
					}

					if (messages.length() < 5000) {
						messages += " Error en línea " + process + 1 + " : ";
						if (e.getMessage()!=null) {
							if (e.getMessage().length() > 100) {
								messages += e.getMessage().substring(0, 100);
							} else {
								messages += e.getMessage();
							}	
						}else{
							messages += "NullPointerException";
						}
						messages += " \r\n ";
					}

					try {
						utx.begin();
						lote.setMessages(messages);
						lote.setProcess(process);
						lote.setErrors(errors);
						loteEao.update(lote);
						utx.commit();
						errors++;
					} catch (Exception e1) {
						e1.printStackTrace();
					}

				}

				process++;
			}

			if (messages.length() > 0) {
				lote.setState("Terminado con errores");
			} else {
				lote.setState("Terminado");
			}
			loteEao.update(lote);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public List<Sale> findSalesBySaleData(Date saleDateStartedAt, Date saleDateEndedAt, Short bankId, Short productId,
			SaleStateEnum saleState) throws ServiceException {
		try {
			return saleEao.findBySaleData(saleDateStartedAt, saleDateEndedAt, bankId, productId, saleState);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Sale> findSalesBySaleDataLimit(Date saleDateStartedAt, Date saleDateEndedAt, Short bankId,
			Short productId, SaleStateEnum saleState, Integer first, Integer limit) throws ServiceException {
		try {
			return saleEao.findBySaleDataLimit(saleDateStartedAt, saleDateEndedAt, bankId, productId, saleState, first,
					limit);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long findSalesBySaleDataCount(Date saleDateStartedAt, Date saleDateEndedAt, Short bankId, Short productId,
			SaleStateEnum saleState) throws ServiceException {
		try {
			return saleEao.findBySaleDataCount(saleDateStartedAt, saleDateEndedAt, bankId, productId, saleState);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	

	@Override
	public List<Sale> findSalesByCreditCardNumber(Long creditCardNumber)
			throws ServiceException {
		try {

			
			  List<Sale> sales = new ArrayList<Sale>();
			  
			  List<Sale> sales1 = saleEao.findByCreditCard(creditCardNumber); 
			  for (Sale sale : sales1) { sales.add(sale); }
			 

			 List<Sale> sales2 = creditCardHistoryEao.findByCreditCardNumber(creditCardNumber);
			
			 for (Sale sale : sales2) { sales.add(sale); }
			 

			return sales;

		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Sale> findSalesByNuicResponsible(Long nuicResponsible) throws ServiceException {
		try {
			return saleEao.findByNuicResponsible(nuicResponsible);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	/*@Override
	public List<Sale> findSalesByNuicResponsibleLimit(Long nuicResponsible, Integer first, Integer limit)
			throws ServiceException {
		try {
			return saleEao.findByNuicResponsibleLimit(nuicResponsible, first, limit);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long findSalesByNuicResponsibleCount(Long nuicResponsible) throws ServiceException {
		try {
			return saleEao.findByNuicResponsibleCount(nuicResponsible);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}*/

	@Override
	public List<Sale> findSalesByNamesResponsible(Long nuicResponsible, String firstnameResponsible,
			String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException {
		try {
			return saleEao.findByNamesResponsible(nuicResponsible, firstnameResponsible, lastnamePaternalResponsible,
					lastnameMaternalResponsible);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	/*@Override
	public List<Sale> findSalesByNamesResponsibleLimit(Long nuicResponsible, String firstnameResponsible,
			String lastnamePaternalResponsible, String lastnameMaternalResponsible, Integer first, Integer limit)
					throws ServiceException {
		try {
			return saleEao.findByNamesResponsibleLimit(nuicResponsible, firstnameResponsible,
					lastnamePaternalResponsible, lastnameMaternalResponsible, first, limit);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long findSalesByNamesResponsibleCount(Long nuicResponsible, String firstnameResponsible,
			String lastnamePaternalResponsible, String lastnameMaternalResponsible) throws ServiceException {
		try {
			return saleEao.findByNamesResponsibleCount(nuicResponsible, firstnameResponsible,
					lastnamePaternalResponsible, lastnameMaternalResponsible);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}*/

	@Override
	public List<Sale> findSalesByNamesInsured(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured,
			String lastnameMaternalInsured) throws ServiceException {
		try {
			return saleEao.findByNamesInsured(nuicInsured, firstnameInsured, lastnamePaternalInsured,
					lastnameMaternalInsured);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	/*@Override
	public List<Sale> findSalesByNamesInsuredLimit(Long nuicInsured, String firstnameInsured,
			String lastnamePaternalInsured, String lastnameMaternalInsured, Integer first, Integer limit)
					throws ServiceException {
		try {
			return saleEao.findByNamesInsuredLimit(nuicInsured, firstnameInsured, lastnamePaternalInsured,
					lastnameMaternalInsured, first, limit);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long findSalesByNamesInsuredCount(Long nuicInsured, String firstnameInsured, String lastnamePaternalInsured,
			String lastnameMaternalInsured) throws ServiceException {
		try {
			return saleEao.findByNamesInsuredCount(nuicInsured, firstnameInsured, lastnamePaternalInsured,
					lastnameMaternalInsured);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}*/

	@Override
	public List<Sale> findSalesByNamesContractor(Long nuicContractor, String firstnameContractor,
			String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException {
		try {
			return saleEao.findByNamesContractor(nuicContractor, firstnameContractor, lastnamePaternalContractor,
					lastnameMaternalContractor);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	/*@Override
	public List<Sale> findSalesByNamesContractorLimit(Long nuicContractor, String firstnameContractor,
			String lastnamePaternalContractor, String lastnameMaternalContractor, Integer first, Integer limit)
					throws ServiceException {
		try {
			return saleEao.findByNamesContractorLimit(nuicContractor, firstnameContractor, lastnamePaternalContractor,
					lastnameMaternalContractor, first, limit);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}*/

	/*@Override
	public Long findSalesByNamesContractorCount(Long nuicContractor, String firstnameContractor,
			String lastnamePaternalContractor, String lastnameMaternalContractor) throws ServiceException {
		try {
			return saleEao.findByNamesContractorCount(nuicContractor, firstnameContractor, lastnamePaternalContractor,
					lastnameMaternalContractor);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}*/

	@Override
	public long checkIfExistSale(Long nuicInsured, Date dateOfSale, short bankId, short productId,
			short collectionPeriodId) {
		try {
			return saleEao.checkExistSale(nuicInsured, dateOfSale, bankId, productId, collectionPeriodId);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

}
