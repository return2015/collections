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
import com.returnsoft.collection.enumeration.SaleStateEnum;
import com.returnsoft.collection.service.SaleService;
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
	
	private List<Sale> sales;

	public SaleLazyModel(List<Sale> sales) {
		super();
		if (sales!=null) {
			this.sales=sales;
			setRowCount(sales.size());
		}
		
	}
	public SaleLazyModel(SaleService saleService, Date dateOfSaleStarted,Date dateOfSaleEnded, Short bankId, Short productId, SaleStateEnum saleState) {
		super();
		
		this.saleService=saleService;
		this.dateOfSaleStarted=dateOfSaleStarted;
		this.dateOfSaleEnded=dateOfSaleEnded;
		this.bankId=bankId;
		this.productId=productId;
		this.saleState=saleState;
		this.sales=null;
		
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
		// TODO Auto-generated method stub
		return new ArrayList<Sale>();
	}

	@Override
	public List<Sale> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {

		
		
		try {
			if (saleService!=null) {
				
				sales = saleService.findSalesBySaleDataLimit(dateOfSaleStarted, dateOfSaleEnded, bankId,
						productId, saleState, first, pageSize);
				Long salesCount = saleService.findSalesBySaleDataCount(dateOfSaleStarted, dateOfSaleEnded, bankId,
						productId, saleState);
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
