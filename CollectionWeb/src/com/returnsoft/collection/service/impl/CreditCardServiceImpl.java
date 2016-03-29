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

import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.CreditCardHistoryEao;
import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.CreditCardHistory;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.CreditCardService;
import com.returnsoft.collection.vo.CreditCardFile;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class CreditCardServiceImpl implements CreditCardService {

	/*
	 * @EJB private SaleEao saleEao;
	 */

	/*
	 * @EJB private CommerceEao commerceEao;
	 */
	
	@EJB
	private LoteEao loteEao;

	@EJB
	private CreditCardEao creditCardEao;

	@EJB
	private CreditCardHistoryEao creditCardHistoryEao;
	
	@Resource
	private EJBContext context;

	// public CreditCard add(CreditCard creditCard) throws ServiceException{
	// try {
	//
	// creditCardUpdateEao.add(creditCard);
	//
	// Sale sale = saleEao.findById(creditCard.getSale().getId());
	// System.out.println(sale.getId());
	// sale.setCreditCard(creditCard);
	// sale = saleEao.update(sale);
	//
	// return creditCard;
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
	// throw new ServiceException(e.getMessage(), e);
	// }else{
	// throw new ServiceException();
	// }
	// }
	// }
	
	@Asynchronous
	@Override
	public void updateCreditCardList(List<CreditCard> creditCards, CreditCardFile headers, String filename,
			User updatedBy) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();

		try {

			Lote lote = new Lote(filename, creditCards.size(), "En progreso", LoteTypeEnum.UPDATECREDITCARD, updatedBy,
					current);
			loteEao.add(lote);

			for (CreditCard creditCard : creditCards) {
				try {
					utx.begin();

					creditCard.setLote(lote);
					creditCard.setUpdatedBy(updatedBy);
					creditCard.setUpdatedAt(current);
					creditCardEao.update(creditCard);

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
	public CreditCard update(CreditCard creditCard) throws ServiceException {
		try {
			return creditCardEao.update(creditCard);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	@Override
	public List<CreditCardHistory> findBySale(Long saleId) throws ServiceException {
		try {
			return creditCardHistoryEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

}
