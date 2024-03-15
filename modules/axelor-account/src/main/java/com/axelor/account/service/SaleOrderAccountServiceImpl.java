package com.axelor.account.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.axelor.invoice.db.repo.InvoiceRepository;
import com.axelor.sales.db.SaleOrder;
import com.axelor.sales.db.SaleOrderLine;
import com.axelor.sales.db.repo.SaleOrderRepository;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

public class SaleOrderAccountServiceImpl implements SaleOrderAccountService {	
	
	private final SaleOrderRepository saleOrderRepository;

    @Inject
    public SaleOrderAccountServiceImpl(SaleOrderRepository saleOrderRepository) {
        this.saleOrderRepository = saleOrderRepository;
    }
    
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Invoice generateInvoiceFromSaleOrder(SaleOrder saleOrder) {

    	Invoice invoice = new Invoice();
        List<SaleOrderLine> saleOrderLines = saleOrder.getSaleOrderLineList();
        List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
        
        invoice.setSaleOrder(saleOrder);
        invoice.setStatusSelect(InvoiceRepository.STATUS_DRAFT);
        invoice.setCustomer(saleOrder.getCustomer());
        invoice.setInvoiceDateT(saleOrder.getEstimatedInvoiceDate().atStartOfDay());
        
        BigDecimal totalInTaxTotal = BigDecimal.ZERO;
        for (SaleOrderLine saleOrderLine : saleOrderLines) {
            BigDecimal exTaxTotal = saleOrderLine.getExTaxTotal();
            BigDecimal taxRate = saleOrderLine.getTaxRate();
            BigDecimal inTaxTotal = exTaxTotal.multiply(BigDecimal.ONE.add(taxRate));

            totalInTaxTotal = totalInTaxTotal.add(inTaxTotal);
            InvoiceLine invoiceLine = new InvoiceLine();
            invoiceLine.setProduct(saleOrderLine.getProduct());
            invoiceLine.setDescription(saleOrderLine.getProduct().getName());
            invoiceLine.setUnitPriceUntaxed(saleOrderLine.getUnitPriceUntaxed());
            invoiceLine.setExTaxTotal(exTaxTotal);
            invoiceLine.setQuantity(saleOrderLine.getQuantity());
            invoiceLine.setTaxRate(taxRate);
            invoiceLine.setInvoice(invoice);
            invoiceLine.setInTaxTotal(inTaxTotal);
            
            invoiceLineList.add(invoiceLine);
        }
        
        invoice.setInvoiceLineList(invoiceLineList);

        invoice.setInTaxTotal(totalInTaxTotal);

        try {
            SaleOrder fetchedSaleOrder = saleOrderRepository.find(saleOrder.getId());
            if (fetchedSaleOrder == null) {
                throw new RuntimeException("SaleOrder not found with ID: " + saleOrder.getId());
            }
            return Beans.get(InvoiceRepository.class).save(invoice);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while generating the invoice: " + e.getMessage(), e);
        }
    }
}
