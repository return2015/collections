package com.returnsoft.collection.service.impl;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.LoteService;

@Stateless
public class LoteServiceImpl implements LoteService {

	@EJB
	private LoteEao loteEao;
	
	@Override
	public List<Lote> findByDate(Date date) throws ServiceException {
		try {
			return loteEao.findByDate(date);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}
	
	/*@Asynchronous
	@Override
	public void addTypeCollection(List<Collection> collections, String filename, CollectionFile headers, Integer userId){
		
		Lote lote = new Lote();
		Date date = new Date();

		try {
			
			User user = userEao.findById(userId);

			lote.setName(filename);
			lote.setTotal(collections.size());
			lote.setProcess(0);
			lote.setCreatedAt(date);
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

	}*/

	@Override
	public List<Lote> findLimit(Date date, LoteTypeEnum loteType, Integer first, Integer limit)
			throws ServiceException {
		try {
			return loteEao.findLimit(date, loteType, first, limit);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

	@Override
	public Long findCount(Date date, LoteTypeEnum loteType) throws ServiceException {
		try {
			return loteEao.findCount(date, loteType);
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new ServiceException(e.getClass().getName());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException(e.getMessage());
		}
	}

}
