package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.entity.SaleState;
import com.returnsoft.collection.entity.SaleStateHistory;
import com.returnsoft.collection.entity.User;
import com.returnsoft.collection.exception.ServiceException;
import com.returnsoft.collection.vo.SaleStateFile;

public interface SaleStateService {

	public SaleState update(SaleState saleState) throws ServiceException;
	
	public List<SaleStateHistory> findBySale(Long saleId) throws ServiceException;
	
	public void updateSaleStateList(List<SaleState> saleStates, SaleStateFile headers, String filename, User updatedBy);

}
