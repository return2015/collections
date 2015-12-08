package com.returnsoft.collection.service.impl;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.exception.EaoException;
import com.returnsoft.collection.service.LoteService;

@Stateless
//@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
public class LoteServiceImpl implements LoteService{
	
	@EJB
	private LoteEao loteEao;
	
	
	//@Asynchronous
		@TransactionAttribute(TransactionAttributeType.NEVER)
		public void updateLote(Lote lote){
			
				//userTransaction.begin();
				
				try {
					loteEao.update(lote);
				} catch (EaoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				//userTransaction.commit();
			
		}
		
		@TransactionAttribute(TransactionAttributeType.NEVER)
		public Lote createLote(String name, Integer total){
			
				//userTransaction.begin();
			
			Lote lote = new Lote();
			lote.setName(name);
			lote.setTotal(total);
			lote.setProcess(0);
			lote.setState("En progreso");
			
				try {
					loteEao.add(lote);
					
				} catch (EaoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (lote.getId()!=null && lote.getId()>0) {
					return lote;
				}else{
					return null;
				}

				//userTransaction.commit();
			
		}
		

}
