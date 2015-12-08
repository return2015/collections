package com.returnsoft.collection.service;

import java.util.List;

import com.returnsoft.collection.controller.SaleFile;

public interface SaleServiceBackground {
	
	public void add(List<SaleFile> dataList, String filename, SaleFile headers, Integer userId, Short bankId);

}
