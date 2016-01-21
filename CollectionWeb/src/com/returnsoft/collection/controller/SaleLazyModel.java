package com.returnsoft.collection.controller;

import java.util.ArrayList;
import java.util.Date;
//import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
//import javax.ejb.Singleton;
import javax.inject.Singleton;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleService;
//@Singleton
public class SaleLazyModel extends LazyDataModel<Sale>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5095693738556548196L;
	
	//@EJB
	private SaleService saleService;
	
	//DATOS DE VENTA
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;
	private Short bankId;
	private Short productId;
	private SaleStateEnum saleState;
	
	//DATOS PERSONALES DE CONTRATANTE
	/*sales = saleService.findSalesByNamesContractor(nuicContractorLong, firstnameContractor,
	lastnamePaternalContractor, lastnameMaternalContractor);*/
	private String searchType;
	private String personType;
	private Long nuic;
	private String firstname;
	private String lastnamePaternal;
	private String lastnameMaternal;
	
	private Long number;
	
	private String SALESDATA="searchData";
	private String PERSONALDATA="personalData";
	private String DNI="dni";
	private String CREDITCARD="creditCard";
	private String NOTIFICATIONDATA="notificationData";
	
	///////////////
	
	private Date sendingDate;
	private List<NotificationStateEnum> notificationStates;
	//private Short bankId;
	//private SaleStateEnum saleState; 
	private NotificationTypeEnum notificationType; 
	private Boolean withoutMail; 
	private Boolean withoutAddress;
	private Boolean withoutNotification;
	private String orderNumber;
	
	public SaleLazyModel(SaleService saleService, String numberType, Long number) {
		super();
		System.out.println("ingreso a SaleLazyModel");
		this.saleService=saleService;
		
		if (numberType.equals("dni")) {
			this.number=number;
			this.searchType=DNI;
		}else if(numberType.equals("creditCard")){
			this.number=number;
			this.searchType=CREDITCARD;	
		}
		
	}
	
	
	
	
	public SaleLazyModel(SaleService saleService, String personType,Long nuic, String firstname, String lastnamePaternal, String lastnameMaternal) {
		super();
		System.out.println("ingreso a SaleLazyModel");
		this.saleService=saleService;
		this.personType=personType;
		this.nuic=nuic;
		this.firstname=firstname;
		this.lastnamePaternal=lastnamePaternal;
		this.lastnameMaternal=lastnameMaternal;
		this.searchType=PERSONALDATA;
	}
	
	

	public SaleLazyModel(SaleService saleService, Date dateOfSaleStarted,Date dateOfSaleEnded, Short bankId, Short productId, SaleStateEnum saleState) {
		super();
		System.out.println("ingreso a SaleLazyModel");
		this.saleService=saleService;
		this.dateOfSaleStarted=dateOfSaleStarted;
		this.dateOfSaleEnded=dateOfSaleEnded;
		this.bankId=bankId;
		this.productId=productId;
		this.saleState=saleState;
		this.searchType=SALESDATA;
	}
	
	//ublic List<Sale> findForNotifications(Date saleDateStartedAt,Date saleDateEndedAtInteger first, Integer limit) throws ServiceException {

		public SaleLazyModel(SaleService saleService, Date dateOfSaleStarted,Date dateOfSaleEnded,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification, String orderNumber) {
			super();
			System.out.println("ingreso a SaleLazyModel");
			this.saleService=saleService;
			this.dateOfSaleStarted=dateOfSaleStarted;
			this.dateOfSaleEnded=dateOfSaleEnded;
			this.bankId=bankId;
			this.sendingDate=sendingDate;
			this.notificationStates=notificationStates;
			//private Short bankId;
			this.saleState=saleState; 
			this.notificationType=notificationType; 
			this.withoutMail=withoutMail; 
			this.withoutAddress=withoutAddress;
			this.withoutNotification=withoutNotification;
			this.orderNumber=orderNumber;
			this.searchType=NOTIFICATIONDATA;
		}
	@Override
	public int getPageSize() {
		// TODO Auto-generated method stub
		return super.getPageSize();
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return super.getRowCount();
	}

	@Override
	public Sale getRowData() {
		// TODO Auto-generated method stub
		return super.getRowData();
	}

	@Override
	public Sale getRowData(String rowKey) {
		// TODO Auto-generated method stub
		Long saleId = Long.parseLong(rowKey);
		List<Sale> list = (List<Sale>) getWrappedData();
		for (Sale sale : list) {
			if (sale.getId().equals(saleId)) {
				return sale;
			}
		}

		return null;
		//return super.getRowData(rowKey);
	}

	@Override
	public int getRowIndex() {
		// TODO Auto-generated method stub
		return super.getRowIndex();
	}

	@Override
	public Object getRowKey(Sale object) {
		// TODO Auto-generated method stub
		//return super.getRowKey(object);
		return object.getId();
	}

	@Override
	public Object getWrappedData() {
		// TODO Auto-generated method stub
		return super.getWrappedData();
	}

	@Override
	public boolean isRowAvailable() {
		// TODO Auto-generated method stub
		return super.isRowAvailable();
	}

	@Override
	public List<Sale> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		// TODO Auto-generated method stub
		System.out.println("ingreso a load1");
		return new ArrayList<Sale>();
	}

	@Override
	public List<Sale> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		// TODO Auto-generated method stub
		System.out.println("ingreso a load2");
		List<Sale> sales = null;
		//System.out.println("first"+first);
		//System.out.println("pageSize"+pageSize);
		
		
		try {
			if (searchType.equals(SALESDATA)) {
				
				sales = saleService.findSalesBySaleDataLimit(dateOfSaleStarted, dateOfSaleEnded, bankId,
						productId, saleState, first, pageSize);
				Long salesCount = saleService.findSalesBySaleDataCount(dateOfSaleStarted, dateOfSaleEnded, bankId,
						productId, saleState);
				this.setRowCount(salesCount.intValue());
				
			}else if (searchType.equals(PERSONALDATA)) {
				
				if (personType.equals("contractor")) {
					sales = saleService.findSalesByNamesContractorLimit(nuic, firstname,
							lastnamePaternal, lastnameMaternal,first,pageSize);
					Long salesCount = saleService.findSalesByNamesContractorCount(nuic, firstname,
							lastnamePaternal, lastnameMaternal);
					this.setRowCount(salesCount.intValue());
					
				}else if (personType.equals("insured")) {
					sales = saleService.findSalesByNamesInsuredLimit(nuic, firstname,
							lastnamePaternal, lastnameMaternal,first,pageSize);
					Long salesCount = saleService.findSalesByNamesInsuredCount(nuic, firstname,
							lastnamePaternal, lastnameMaternal);
					this.setRowCount(salesCount.intValue());
					
				}else if (personType.equals("responsible")) {
					sales = saleService.findSalesByNamesResponsibleLimit(nuic, firstname,
							lastnamePaternal, lastnameMaternal,first,pageSize);
					Long salesCount = saleService.findSalesByNamesResponsibleCount(nuic, firstname,
							lastnamePaternal, lastnameMaternal);
					this.setRowCount(salesCount.intValue());
				}
				
			}else if (searchType.equals(DNI)) {
				sales = saleService.findSalesByNuicResponsibleLimit(number, first, pageSize);
				Long salesCount = saleService.findSalesByNuicResponsibleCount(number);
				this.setRowCount(salesCount.intValue());
			}else if (searchType.equals(CREDITCARD)) {
				//sales = saleService.findSalesByCreditCardNumber(number);
			}else if (searchType.equals(NOTIFICATIONDATA)) {
				sales = saleService.findForNotificationsLimit(dateOfSaleStarted, dateOfSaleEnded, sendingDate, notificationStates, bankId, saleState, notificationType, withoutMail, withoutAddress, withoutNotification, orderNumber,first, pageSize);
				Long salesCount = saleService.findForNotificationsCount(dateOfSaleStarted, dateOfSaleEnded, sendingDate, notificationStates, bankId, saleState, notificationType, withoutMail, withoutAddress, withoutNotification,orderNumber);
				this.setRowCount(salesCount.intValue());
			}
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return sales;
	}

	@Override
	public void setPageSize(int pageSize) {
		// TODO Auto-generated method stub
		super.setPageSize(pageSize);
	}

	@Override
	public void setRowCount(int rowCount) {
		// TODO Auto-generated method stub
		super.setRowCount(rowCount);
	}

	@Override
	public void setRowIndex(int arg0) {
		// TODO Auto-generated method stub
		super.setRowIndex(arg0);
	}

	@Override
	public void setWrappedData(Object list) {
		// TODO Auto-generated method stub
		super.setWrappedData(list);
	}

	
	
}
