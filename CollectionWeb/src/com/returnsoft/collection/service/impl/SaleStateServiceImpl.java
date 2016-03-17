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

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.eao.SaleStateHistoryEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleStateService;
import com.returnsoft.collection.util.SaleStateFile;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SaleStateServiceImpl implements SaleStateService {


	@EJB
	private LoteEao loteEao;

	@EJB
	private SaleStateEao saleStateEao;

	@EJB
	private SaleStateHistoryEao saleStateHistoryEao;

	@Resource
	private EJBContext context;

	@Asynchronous
	@Override
	public void updateSaleStateList(List<SaleState> saleStates, SaleStateFile headers, String filename,
			User updatedBy) {

		Date current = new Date();
		Integer process = 1;
		Integer errors = 1;
		String messages = "";
		UserTransaction utx = context.getUserTransaction();

		try {

			Lote lote = new Lote(filename, saleStates.size(), "En progreso", LoteTypeEnum.UPDATESALESTATE, updatedBy,
					current);
			loteEao.add(lote);

			for (SaleState saleState : saleStates) {
				try {
					utx.begin();

					saleState.setLote(lote);
					saleState.setUpdatedBy(updatedBy);
					saleState.setUpdatedAt(current);
					saleStateEao.update(saleState);

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
	public SaleState update(SaleState saleState) throws ServiceException {
		try {
			return saleStateEao.update(saleState);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public List<SaleStateHistory> findBySale(Long saleId) throws ServiceException {
		try {
			return saleStateHistoryEao.findBySaleId(saleId);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

}
