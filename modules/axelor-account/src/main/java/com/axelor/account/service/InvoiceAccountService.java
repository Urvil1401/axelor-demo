package com.axelor.account.service;

import com.axelor.account.db.Move;
import com.axelor.invoice.db.Invoice;

public interface InvoiceAccountService {

	public Move generateMoveFromInvoice(Invoice invoice);
	
}
