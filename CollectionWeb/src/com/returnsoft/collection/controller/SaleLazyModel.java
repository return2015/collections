package com.returnsoft.collection.controller;

import java.util.Date;
//import java.sql.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.returnsoft.collection.entity.Sale;
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.service.SaleService;
//@Stateless
public class SaleLazyModel extends LazyDataModel<Sale>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5095693738556548196L;
	
	//@EJB
	private SaleService saleService;
	
	private Date dateOfSaleStarted;
	private Date dateOfSaleEnded;
	private short bankId;
	private short productId;
	private SaleStateEnum saleState;
	

	public SaleLazyModel(Date dateOfSaleStarted,Date dateOfSaleEnded, short bankId, short productId, SaleStateEnum saleState) {
		//super();
		System.out.println("ingreso a SaleLazyModel");
		this.dateOfSaleStarted=dateOfSaleStarted;
		this.dateOfSaleEnded=dateOfSaleEnded;
		this.bankId=bankId;
		this.productId=productId;
		this.saleState=saleState;
		
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
		return super.getRowData(rowKey);
	}

	@Override
	public int getRowIndex() {
		// TODO Auto-generated method stub
		return super.getRowIndex();
	}

	@Override
	public Object getRowKey(Sale object) {
		// TODO Auto-generated method stub
		return super.getRowKey(object);
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
		return super.load(first, pageSize, multiSortMeta, filters);
	}

	@Override
	public List<Sale> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		// TODO Auto-generated method stub
		System.out.println("ingreso a load2");
		List<Sale> sales = null;
		System.out.println("first"+first);
		System.out.println("pageSize"+pageSize);
		
		
		try {
			sales = saleService.findSalesBySaleData(dateOfSaleStarted, dateOfSaleEnded, bankId,
					productId, saleState);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
		return sales;
		//return super.load(first, pageSize, sortField, sortOrder, filters);
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
