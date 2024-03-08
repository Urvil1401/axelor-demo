package com.axelor.invoice.service;

import java.util.List;
import java.util.Map;

public interface InvoiceService {

	public void setStatusCancel(List<Integer> list);
	
	public void invoiceMergeBtn(List<Long> list);
	
}
