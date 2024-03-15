package com.axelor.account.service;

import com.axelor.invoice.db.Invoice;
import com.axelor.sales.db.SaleOrder;

public interface SaleOrderAccountService {

	public Invoice generateInvoiceFromSaleOrder(SaleOrder saleOrder);

}
