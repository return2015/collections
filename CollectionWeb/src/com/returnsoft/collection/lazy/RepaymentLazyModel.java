package com.returnsoft.collection.lazy;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.returnsoft.collection.entity.Repayment;
import com.returnsoft.collection.service.RepaymentService;


public class RepaymentLazyModel extends LazyDataModel<Repayment>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3190797265203335499L;
	
	private RepaymentService repaymentService;
	/*private Date createdAtStarted;
	private Date createdAtEnded;*/
	private Short bankId;
	private Short productId;
	private Long documentNumber;
	private Date returnedDate;
	private Date paymentDate;
	
	public RepaymentLazyModel(RepaymentService repaymentService, Date returnedDate, Date paymentDate
			, Short bankId, Short productId, Long documentNumber) {
		super();
		this.repaymentService = repaymentService;
		this.bankId=bankId;
		this.productId=productId;
		this.documentNumber=documentNumber;
		this.returnedDate=returnedDate;
		this.paymentDate=paymentDate;
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
	public Repayment getRowData() {
		// TODO Auto-generated method stub
		return super.getRowData();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Repayment getRowData(String rowKey) {
		Long collectionId = Long.parseLong(rowKey);
		List<Repayment> list = (List<Repayment>) getWrappedData();
		for (Repayment collection : list) {
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
	public Object getRowKey(Repayment object) {
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
	public List<Repayment> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		// TODO Auto-generated method stub
		return super.load(first, pageSize, multiSortMeta, filters);
	}

	@Override
	public List<Repayment> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		
		List<Repayment> repayments = null;
		
		try {
			
			repayments = repaymentService.findLimit(paymentDate, returnedDate, bankId, productId, documentNumber, first, pageSize);
			Long collectionsCount = repaymentService.findCount(paymentDate, returnedDate, bankId, productId, documentNumber);
			this.setRowCount(collectionsCount.intValue());	
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return repayments;
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
