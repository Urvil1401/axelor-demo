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

		for (Integer listItem : list) {
			Long id = (long) listItem;
			Invoice invoice = Beans.get(InvoiceRepository.class).find(id);
			if (invoice.getStatusSelect() != 2) {
				invoice.setStatusSelect(3);
			}
			Beans.get(InvoiceRepository.class).save(invoice);
		}
	}

	@Transactional(rollbackOn = Exception.class)
	@Override
	public void invoiceMergeBtn(List<Long> list) {

		Invoice firstInvoice = Beans.get(InvoiceRepository.class).find(list.get(0));

		for (Long invoiceId : list) {
			Invoice invoice = Beans.get(InvoiceRepository.class).find(invoiceId);

			if (!invoice.getCustomer().equals(firstInvoice.getCustomer())) {
				throw new IllegalArgumentException("You have to choose invoices with the same customer");
			}
		}

		Invoice newInvoice = new Invoice();
		newInvoice.setCustomer(firstInvoice.getCustomer());
		newInvoice.setInvoiceDateT(LocalDateTime.now());
		newInvoice.setStatusSelect(0);

		for (Long invoiceId : list) {
			Invoice invoice = Beans.get(InvoiceRepository.class).find(invoiceId);
			invoice.setGeneratedInvoice(newInvoice);
			invoice.setArchived(true);
			Beans.get(InvoiceRepository.class).save(invoice);
		}

		List<Invoice> invoiceList = new ArrayList<Invoice>();
		for (long i : list) {
			invoiceList.add(Beans.get(InvoiceRepository.class).find(i));
		}

		Map<Product, List<InvoiceLine>> invoiceLinesByProduct = new HashMap<>();

		for (Invoice i : invoiceList) {
			List<InvoiceLine> inLine = i.getInvoiceLineList();

			for (InvoiceLine j : inLine) {
				invoiceLinesByProduct.put(j.getProduct(), inLine);
			}
		}

		System.out.println(invoiceLinesByProduct.keySet());
		System.out.println(invoiceLinesByProduct.values());

		Map<Product, List<InvoiceLine>> invoiceLinesByProduct1 = new HashMap<>();

		for (Invoice invoice : invoiceList) {
			List<InvoiceLine> invoiceLines = invoice.getInvoiceLineList();

			for (InvoiceLine invoiceLine : invoiceLines) {
				Product product = invoiceLine.getProduct();

				if (invoiceLinesByProduct1.containsKey(product)) {
					List<InvoiceLine> existingLines = invoiceLinesByProduct1.get(product);
					boolean productMatch = false;

					for (InvoiceLine existingLine : existingLines) {
						if (existingLine.getUnitPriceUntaxed().equals(invoiceLine.getUnitPriceUntaxed())
								&& existingLine.getTaxRate().equals(invoiceLine.getTaxRate())
								&& existingLine.getDescription().equals(invoiceLine.getDescription())) {
							existingLine.setQuantity(existingLine.getQuantity().add(invoiceLine.getQuantity()));
							existingLine.setExTaxTotal(existingLine.getExTaxTotal().add(invoiceLine.getExTaxTotal()));
							existingLine.setInTaxTotal(existingLine.getInTaxTotal().add(invoiceLine.getInTaxTotal()));
							productMatch = true;
							break;
						}
					}

					if (!productMatch) {
						invoiceLinesByProduct1.get(product).add(invoiceLine);
					}
				} else {
					List<InvoiceLine> newLineList = new ArrayList<>();
					newLineList.add(invoiceLine);
					invoiceLinesByProduct1.put(product, newLineList);
				}
			}
		}
		
		System.out.println("Invoice lines grouped and merged by product:");
		for (Map.Entry<Product, List<InvoiceLine>> entry : invoiceLinesByProduct1.entrySet()) {
		    System.out.println("Product: " + entry.getKey());
		    System.out.println("Invoice Lines:");
		    for (InvoiceLine invoiceLine : entry.getValue()) {
		        System.out.println("\t" + invoiceLine);
		    }
		}

	}

}
