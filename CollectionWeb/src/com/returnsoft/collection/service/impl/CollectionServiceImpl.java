package com.returnsoft.collection.service.impl;

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

import com.returnsoft.collection.eao.CollectionEao;
import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CollectionService;
import com.returnsoft.collection.vo.CollectionFile;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CollectionServiceImpl implements CollectionService {

	@EJB
	private SaleEao saleEao;
	
	@EJB
	private LoteEao loteEao;

	@EJB
	private CollectionEao collectionEao;
	
	@Resource
	private EJBContext context;

	@Override
	public void add(Collection collection) throws ServiceException {
		try {
			collectionEao.add(collection);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}
	
	@Asynchronous
	@Override
	public void addCollectionList(List<Collection> collections, CollectionFile headers, String filename, User createdBy) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();

		try {

			Lote lote = new Lote(filename, collections.size(), "En progreso", LoteTypeEnum.CREATECOLLECTION, createdBy, current);
			loteEao.add(lote);

			for (Collection collection : collections) {

				try {

					utx.begin();
					System.out.println(collection.getDepositDate());
					collection.setLote(lote);
					collection.setCreatedBy(createdBy);
					collection.setCreatedAt(current);
					collectionEao.add(collection);

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

	/*@Override
	public Integer findByResponseAndAuthorizationDay(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode)
			throws ServiceException {
		try {
			return collectionEao
					.findByResponseAndAuthorizationDay(messageResponse,authorizationDate,saleCode);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}

	}
	
	@Override
	public Integer findByResponseAndAuthorizationMonth(CollectionResponseEnum messageResponse, Date authorizationDate, String saleCode)
			throws ServiceException {
		try {
			return collectionEao.findByResponseAndAuthorizationMonth(messageResponse,authorizationDate,saleCode);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}

	}
	
	@Override
	public List<Collection> findAllowsBySale(Long saleId) throws ServiceException{
		try {
			return collectionEao.findAllowsBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}*/
	
	@Override
	public List<Collection> findBySale(Long saleId) throws ServiceException{
		try {
			return collectionEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());	
		}
	}
	
	@Override
	public List<Collection> findList(Date estimatedDate, Date depositDate,Date monthLiquidationDate,Short bankId, Short productId,Long documentNumber)
			throws ServiceException {
		try {
			System.out.println("INICIA FIND LIST");
			
			List<Collection> collections = collectionEao.findList(estimatedDate, depositDate,monthLiquidationDate, bankId,productId,documentNumber);
			
			System.out.println("TERMINA FIND LIST");
			
			return collections;
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Collection> findLimit(Date estimatedDate, Date depositDate,Date monthLiquidationDate,Short bankId, Short productId,Long documentNumber, Integer first, Integer limit)
			throws ServiceException {
		try {
			
			return collectionEao.findLimit(estimatedDate, depositDate,monthLiquidationDate, bankId,productId,documentNumber,first, limit);
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long findCount(Date estimatedDate, Date depositDate,Date monthLiquidationDate,Short bankId, Short productId,Long documentNumber) throws ServiceException {
		try {
			
			return collectionEao.findCount(estimatedDate, depositDate,monthLiquidationDate,bankId,productId,documentNumber);
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public long checkIfExist(Date estimatedDate, String saleCode) {
		try {
			return collectionEao.checkExists(saleCode,estimatedDate);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	

}
