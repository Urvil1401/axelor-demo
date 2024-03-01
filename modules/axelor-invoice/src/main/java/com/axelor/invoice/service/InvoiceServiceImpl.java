package com.axelor.invoice.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.invoice.db.repo.InvoiceRepository;
import com.axelor.sales.db.Product;
import com.google.inject.persist.Transactional;

public class InvoiceServiceImpl implements InvoiceService {

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void setStatusCancel(List<Integer> list) {
		
		for(Integer listItem : list) {
            Long id = (long) listItem;
            Invoice invoice = Beans.get(InvoiceRepository.class).find(id);
            if(invoice.getStatusSelect()!=2) {                
                invoice.setStatusSelect(3);
            }
            Beans.get(InvoiceRepository.class).save(invoice);
        }
	}
	
	@Transactional(rollbackOn = Exception.class)
	@Override
	public void invoiceMergeBtn(Map<String, Object> paramMap) {
		
	    @SuppressWarnings("unchecked")
	    List<Long> selectedInvoiceIds = (List<Long>) paramMap.get("list");

	    if (selectedInvoiceIds.size() < 2) {
	        System.out.println("Please select at least two invoices");
	        return;
	    }

	    Long firstInvoiceId = selectedInvoiceIds.get(0);
	    Invoice firstInvoice = Beans.get(InvoiceRepository.class).find(firstInvoiceId);

	    for (Long invoiceId : selectedInvoiceIds) {
	        Invoice invoice = Beans.get(InvoiceRepository.class).find(invoiceId);
	        if (!invoice.getCustomer().equals(firstInvoice.getCustomer())) {
	            System.out.println("You have to choose invoices with the same customer");
	            return;
	        }
	    }

	    Invoice newInvoice = new Invoice();
	    newInvoice.setCustomer(firstInvoice.getCustomer());
	    newInvoice.setInvoiceDateT(LocalDateTime.now());
	    newInvoice.setStatusSelect(0);
	    
	    System.out.println(newInvoice);

	    for (Long invoiceId : selectedInvoiceIds) {
	        Invoice invoice = Beans.get(InvoiceRepository.class).find(invoiceId);
	        invoice.setGeneratedInvoice(newInvoice);
	        invoice.setArchived(true);
	        Beans.get(InvoiceRepository.class).save(invoice);
	    }
	    
	    Map<Product, List<InvoiceLine>> invoiceLinesByProduct = new HashMap<>();

	    for (InvoiceLine invoiceLine : newInvoice.getInvoiceLineList()) {
	        Product product = invoiceLine.getProduct();
	        
	        if (invoiceLinesByProduct.containsKey(product)) {
	            invoiceLinesByProduct.get(product).add(invoiceLine);
	        } else {
	            List<InvoiceLine> linesForProduct = new ArrayList<>();
	            linesForProduct.add(invoiceLine);
	            invoiceLinesByProduct.put(product, linesForProduct);
	        }
	    }
 
	}

}
