package com.returnsoft.collection.lazy;

import java.util.ArrayList;
import java.util.Date;
//import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.NotificationStateEnum;
import com.returnsoft.collection.enumeration.NotificationTypeEnum;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.service.NotificationService;
public class NotificationsBySaleLazyModel extends LazyDataModel<Sale>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5095693738556548196L;
	
	//@EJB
	private NotificationService notificationService;
	
	//DATOS DE VENTA
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;
	private Short bankId;
	private SaleStateEnum saleState;
	
	///////////////
	
	private Date sendingDate;
	private List<NotificationStateEnum> notificationStates;
	private NotificationTypeEnum notificationType; 
	private Boolean withoutMail; 
	private Boolean withoutAddress;
	private Boolean withoutNotification;
	private String orderNumber;
	
	public NotificationsBySaleLazyModel() {
		super();
		
	}
	
		public NotificationsBySaleLazyModel(NotificationService notificationService, Date dateOfSaleStarted,Date dateOfSaleEnded,Date sendingDate, List<NotificationStateEnum> notificationStates, Short bankId, SaleStateEnum saleState, NotificationTypeEnum notificationType, Boolean withoutMail, Boolean withoutAddress, Boolean withoutNotification, String orderNumber) {
			super();
			System.out.println("ingreso a SaleLazyModel");
			this.notificationService=notificationService;
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

	@SuppressWarnings("unchecked")
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
		
		return new ArrayList<Sale>();
	}

	@Override
	public List<Sale> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		List<Sale> sales = null;
		
		try {
			
			if (notificationService!=null) {
				sales = notificationService.findBySaleLimit(dateOfSaleStarted, dateOfSaleEnded, sendingDate, notificationStates, bankId, saleState, notificationType, withoutMail, withoutAddress, withoutNotification, orderNumber,first, pageSize);
				Long salesCount = notificationService.findBySaleCount(dateOfSaleStarted, dateOfSaleEnded, sendingDate, notificationStates, bankId, saleState, notificationType, withoutMail, withoutAddress, withoutNotification,orderNumber);
				this.setRowCount(salesCount.intValue());
			}
			
		} catch (Exception e) {
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
