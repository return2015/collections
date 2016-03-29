package com.returnsoft.collection.lazy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import com.returnsoft.collection.entity.Lote;
import com.returnsoft.collection.enumeration.LoteTypeEnum;
import com.returnsoft.collection.service.LoteService;

public class LoteLazyModel extends LazyDataModel<Lote> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7850339086642711235L;
	
	private LoteService loteService;
	private Date date;
	private LoteTypeEnum loteType;

	public LoteLazyModel(LoteService loteService, Date date, LoteTypeEnum loteType) {
		super();

		this.loteService = loteService;
		this.date = date;
		this.loteType = loteType;

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
	public Lote getRowData() {
		// TODO Auto-generated method stub
		return super.getRowData();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Lote getRowData(String rowKey) {
		// TODO Auto-generated method stub
		Long loteId = Long.parseLong(rowKey);
		List<Lote> list = (List<Lote>) getWrappedData();
		for (Lote lote : list) {
			if (lote.getId().equals(loteId)) {
				return lote;
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
	public Object getRowKey(Lote object) {
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
	public List<Lote> load(int first, int pageSize, List<SortMeta> multiSortMeta, Map<String, Object> filters) {
		// TODO Auto-generated method stub
		//return super.load(first, pageSize, multiSortMeta, filters);
		System.out.println("ingreso 1");
		return new ArrayList<Lote>();
	}

	@Override
	public List<Lote> load(int first, int pageSize, String sortField, SortOrder sortOrder,
			Map<String, Object> filters) {
		System.out.println("ingreso 2");
		try {
			
			List<Lote> lotes = null;
			lotes = loteService.findLimit(date, loteType, first, pageSize);
			Long lotesCount = loteService.findCount(date, loteType);
			this.setRowCount(lotesCount.intValue());
			
			return lotes;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		
		
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
