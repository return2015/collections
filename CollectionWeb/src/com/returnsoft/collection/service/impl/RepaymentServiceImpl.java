package com.returnsoft.collection.service.impl;

import java.math.BigDecimal;
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

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.RepaymentEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.RepaymentService;
import com.returnsoft.collection.vo.RepaymentFile;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class RepaymentServiceImpl implements RepaymentService{
	
	@EJB
	private SaleEao saleEao;
	
	@EJB
	private RepaymentEao repaymentEao;
	
	@EJB
	private LoteEao loteEao;
	
	@Resource
	private EJBContext context;
	
	@Asynchronous
	@Override
	public void addRepaymentList(List<Repayment> repayments, RepaymentFile headers, String filename, User createdBy) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();

		try {

			Lote lote = new Lote(filename, repayments.size(), "En progreso", LoteTypeEnum.CREATEREPAYMENTS, createdBy, current);
			loteEao.add(lote);

			for (Repayment repayment : repayments) {

				try {

					utx.begin();
					//System.out.println(collection.getDepositDate());
					repayment.setLote(lote);
					repayment.setCreatedBy(createdBy);
					repayment.setCreatedAt(current);
					repaymentEao.add(repayment);

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
	public long checkIfExist(String saleCode, BigDecimal returnedAmount) {
		try {
			return repaymentEao.checkExists(saleCode,returnedAmount);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	@Override
	public List<Repayment> findList(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber)
			throws ServiceException {
		try {
			//System.out.println("INICIA FIND LIST");
			
			List<Repayment> repayments = repaymentEao.findList(paymentDate, returnedDate, bankId,productId,documentNumber);
			
			//System.out.println("TERMINA FIND LIST");
			
			return repayments;
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<Repayment> findLimit(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber, Integer first, Integer limit)
			throws ServiceException {
		try {
			
			return repaymentEao.findLimit(paymentDate, returnedDate, bankId,productId,documentNumber,first, limit);
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long findCount(Date paymentDate, Date returnedDate,Short bankId, Short productId,Long documentNumber) throws ServiceException {
		try {
			
			return repaymentEao.findCount(paymentDate, returnedDate,bankId,productId,documentNumber);
			
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	/*@Override
	public Repayment findBySaleIdAndReturnedDate(Long saleId, Date returnedDate) throws ServiceException{
		try {
			return repaymentEao.findBySaleIdAndReturnedDate(saleId, returnedDate);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
		
	}*/
	
	/*@Override
	public void add(Repayment repayment) throws ServiceException {
		try {
			Sale sale = saleEao.findByCode(repayment.getCode());
			repayment.setSale(sale);
			repaymentEao.add(repayment);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}*/
	
	@Override
	public List<Repayment> findBySale(Long saleId) throws ServiceException{
		try {
			return repaymentEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
}
