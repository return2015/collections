package com.returnsoft.collection.eao;

import java.util.List;


import com.returnsoft.collection.entity.CollectionPeriod;
import com.returnsoft.collection.exception.EaoException;

public interface CollectionPeriodEao {
	
	public List<CollectionPeriod> getAll() throws EaoException;
	
	public CollectionPeriod findById(Short collectionPeriodId) throws EaoException;

}
