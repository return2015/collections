package com.returnsoft.collection.service;

import com.returnsoft.collection.entity.Lote;

public interface LoteService {
	
	public void updateLote(Lote lote);
	
	public Lote createLote(String name, Integer total);

}
