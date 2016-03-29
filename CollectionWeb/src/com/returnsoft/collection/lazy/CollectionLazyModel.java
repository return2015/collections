package com.returnsoft.collection.lazy;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.returnsoft.collection.entity.Collection;
import com.returnsoft.collection.service.CollectionService;


public class CollectionLazyModel extends LazyDataModel<Collection>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3190797265203335499L;
	
	private CollectionService collectionService;
	/*private Date createdAtStarted;
	private Date createdAtEnded;*/
	private Short bankId;
	private Short productId;
	private Long documentNumber;
	private Date estimatedDate;
	private Date depositDate;
	private Date monthLiquidationDate;
	
	public CollectionLazyModel(CollectionService collectionService, Date estimatedDate, Date depositDate
			,Date monthLiquidationDate, Short bankId, Short productId, Long documentNumber) {
		super();
		this.collectionService = collectionService;
		this.bankId=bankId;
		this.productId=productId;
		this.documentNumber=documentNumber;
		this.estimatedDate=estimatedDate;
		this.depositDate=depositDate;
		this.monthLiquidationDate=monthLiquidationDate;
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
	public Collection getRowData() {
		// TODO Auto-generated method stub
		return super.getRowData();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getRowData(String rowKey) {
		Long collectionId = Long.parseLong(rowKey);
		List<Collection> list = (List<Collection>) getWrappedData();
		for (Collection collection : list) {
			if (collection.getId().equals(collectionId)) {
				return collection;
			}
		}

		return null;
	}

	@Override
	public int getRowIndex() {
		// TODO Auto-generated method stub
		return super.getRowIndex();
	}

	@Override
	public Object getRowKey(Collection object) {
		// TODO Auto-generated method stub
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
	public List<Collection> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return super.load(first, pageSize, multiSortMeta, filters);
	}

	@Override
	public List<Collection> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		List<Collection> collections = null;
		
		try {
			
			
			collections = collectionService.findLimit(estimatedDate, depositDate,monthLiquidationDate, bankId, productId, documentNumber, first, pageSize);
			Long collectionsCount = collectionService.findCount(estimatedDate, depositDate,monthLiquidationDate, bankId, productId, documentNumber);
			this.setRowCount(collectionsCount.intValue());	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return collections;
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
