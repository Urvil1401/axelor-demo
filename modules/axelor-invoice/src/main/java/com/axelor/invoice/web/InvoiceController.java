package com.axelor.invoice.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.invoice.service.InvoiceService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class InvoiceController{
   
	public void invoiceDate(ActionRequest request, ActionResponse response) {
        response.setValue("invoiceDateT", LocalDateTime.now());
	}
	
    public void taxTotal(ActionRequest request, ActionResponse response) {
        Invoice invoice = request.getContext().asType(Invoice.class);
        BigDecimal inTaxTotal = new BigDecimal(0);
        BigDecimal exTaxTotal = new BigDecimal(0);
        BigDecimal UnitPriceUntaxed = new BigDecimal(0);

        for (int i = 0; i < invoice.getInvoiceLineList().size(); i++) {
        	UnitPriceUntaxed = invoice.getInvoiceLineList().get(i).getUnitPriceUntaxed();
            exTaxTotal = exTaxTotal.setScale(2).add(UnitPriceUntaxed)
                    .multiply(invoice.getInvoiceLineList().get(i).getQuantity());
            inTaxTotal = exTaxTotal.add(exTaxTotal.multiply(invoice.getInvoiceLineList().get(i).getTaxRate()));
        }
        response.setValue("inTaxTotal", inTaxTotal);
        response.setValue("exTaxTotal", exTaxTotal);
    }
    
    public void validate(ActionRequest request, ActionResponse response) {
        Invoice invoice = request.getContext().asType(Invoice.class);
        if (invoice.getInvoiceLineList().isEmpty()) {
            response.setAlert("Empty List!!", "At least one invoice line is required");
        } 
        for(InvoiceLine invoiceLine:invoice.getInvoiceLineList())
        {
            if (invoiceLine.getExTaxTotal().intValue() <= 0) {
                response.setAlert("One invoice line has a null or negative total");
            }
        }    
        taxTotal(request, response);
        response.setValue("statusSelect", 1);
    }
    public void ventilate(ActionRequest request, ActionResponse response) {
    	validate(request, response);
        response.setValue("statusSelect", 2);
    }

    public void validateCancel(ActionRequest request, ActionResponse response) {
        response.setAlert("This action will cancel this invoice.Do you want to proceed?");
    }
    
    public void cancel(ActionRequest request, ActionResponse response) {
        response.setValue("statusSelect", 3);
        taxTotal(request, response);    
    }
    
    public void setStatusCancel(ActionRequest request, ActionResponse response) {
    	
    	Context context = request.getContext();
         
        @SuppressWarnings("unchecked")
		List<Integer> ids = (List<Integer>) context.get("_ids");
         if(ids == null || ids.size() == 0) {
             System.out.println("Empty list is here");
             return;
        }
        Beans.get(InvoiceService.class).setStatusCancel(ids);   
    }

    public void invoiceMergeBtn(ActionRequest request, ActionResponse response) {
        Map<String, Object> context = request.getContext();
        @SuppressWarnings("unchecked")
        ArrayList<LinkedHashMap<String, Object>> selectedInvoiceIds = (ArrayList<LinkedHashMap<String, Object>>) context.get("invoiceLineMergeList");

        if (selectedInvoiceIds.size() < 2) {
            response.setError("Please select at least one invoices");
            return;
        }
        
        List<Long> longList = new ArrayList<>();
        for (LinkedHashMap<String, Object> invoiceMap : selectedInvoiceIds) {
            Integer id = (Integer) invoiceMap.get("id");
            longList.add(id.longValue());
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("list", longList);

        Beans.get(InvoiceService.class).invoiceMergeBtn(longList);   
    }
  
}
