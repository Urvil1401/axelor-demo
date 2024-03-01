package com.axelor.invoice.web;

import java.math.BigDecimal;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;
import com.axelor.sales.db.Product;

public class InvoiceLineController {
    
	public void settaxRate(ActionRequest request, ActionResponse response) {
        response.setValue("taxRate", 0.2);
    }

    public void hidetaxRate(ActionRequest request, ActionResponse response) {
        Context context = request.getContext();
        Invoice invoice = context.getParent().asType(Invoice.class);
        if (invoice.getCustomer().getIsSubjectToTaxes() == false) {
            response.setAttr("taxRate", "hidden", true);
        }
    }

    public void setDesUnitPrice(ActionRequest request, ActionResponse response) {
        InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
        Product product = invoiceLine.getProduct();
        if (product != null) {
            invoiceLine.setDescription(product.getName());
            response.setValue("description", invoiceLine.getDescription());
            invoiceLine.setUnitPriceUntaxed(product.getUnitPriceUntaxed());
            response.setValue("unitPriceUntaxed", invoiceLine.getUnitPriceUntaxed());
        }
    }
    
    public void setexTaxTotal(ActionRequest request, ActionResponse response) {
        InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
        BigDecimal exTaxTotal = invoiceLine.getQuantity().multiply(invoiceLine.getUnitPriceUntaxed());
        response.setValue("exTaxTotal", exTaxTotal);
    }

    public void setinTaxTotal(ActionRequest request, ActionResponse response) {
        InvoiceLine invoiceLine = request.getContext().asType(InvoiceLine.class);
        BigDecimal exTaxTotal = invoiceLine.getExTaxTotal();
        BigDecimal taxRate = invoiceLine.getTaxRate();
        BigDecimal inTaxtotal = exTaxTotal.add(exTaxTotal.multiply(taxRate));
        response.setValue("inTaxtotal", inTaxtotal);
    }	
	
}
