package com.returnsoft.collection.service.impl;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.AccessTimeout;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import com.returnsoft.collection.eao.CreditCardEao;
import com.returnsoft.collection.eao.LoteEao;
import com.returnsoft.collection.eao.PayerEao;
import com.returnsoft.collection.eao.SaleEao;
import com.returnsoft.collection.eao.SaleStateEao;
import com.returnsoft.collection.entity.CreditCard;
import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.entity.Payer;
import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.exception.ServiceException;

@Singleton
@LocalBean
public class SaleServiceBackground {
	
	
	@EJB
	private LoteEao loteEao;
	
	@EJB
	private PayerEao payerEao;
	
	@EJB
	private SaleStateEao saleStateEao;
	
	@EJB
	private CreditCardEao creditCardEao;
	
	@EJB
	private SaleEao saleEao;
	
	@Asynchronous
	@AccessTimeout(value=240000)
	public Future<Integer> add(List<Sale> sales, String filename) throws ServiceException{
		try {
			
			System.out.println("ingreso a addSalesServiceBackground.........");
			
			Lote lote = new Lote();
			lote.setName(filename);
			loteEao.add(lote);
			
			Integer row=1;
			
			for (Sale sale : sales) {
				
				System.out.println("row:"+row);
				
				System.out.println("sale.getPayer().getNuicResponsible():"+sale.getPayer().getNuicResponsible());
				
				SaleState saleState = sale.getSaleState();
				CreditCard creditCard = sale.getCreditCard();
				Payer payer = sale.getPayer();
				
				sale.setSaleState(null);
				sale.setCreditCard(null);
				sale.setPayer(null);
				sale.setLote(lote);
				
				saleEao.add(sale);
				
				//saleState.setSale(sale);
				saleStateEao.add(saleState);
				
				//creditCard.setSale(sale);
				creditCardEao.add(creditCard);
				
				//payer.setSale(sale);
				payerEao.add(payer);
				
				sale.setSaleState(saleState);
				sale.setCreditCard(creditCard);
				sale.setPayer(payer);
				sale.setLote(lote);
				
				row++;
			}
			
			
			 /*try {
		            Thread.sleep(10000);
		            System.out.println("Termino el procesoXXX.... ");
		            row = 10;
		        } catch (InterruptedException e) {
		            e.printStackTrace(); 
		        }*/
			System.out.println("Termino el procesoXXX.... ");
			
			return new AsyncResult<Integer>(row);
			
			
		} catch (Exception e) {
			System.out.println("exception.........");
			System.out.println("exception.........");
			System.out.println("exception.........");
			System.out.println("exception.........");
			System.out.println("exception.........");
			e.printStackTrace();
			//return new AsyncResult<Integer>(0);
			if (e.getMessage()!=null && e.getMessage().trim().length()>0) {
				throw new ServiceException(e.getMessage(), e);	
			}else{
				throw new ServiceException();
			}
		}
	}

}
