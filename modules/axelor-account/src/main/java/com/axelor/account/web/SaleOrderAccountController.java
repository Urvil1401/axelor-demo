package com.axelor.account.web;

import com.axelor.account.service.SaleOrderAccountService;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.sales.db.SaleOrder;
import com.axelor.sales.db.repo.SaleOrderRepository;

public class SaleOrderAccountController {

    public void generateInvoiceFromSaleOrder(ActionRequest request, ActionResponse response) {
        SaleOrder saleOrder = request.getContext().asType(SaleOrder.class);
        saleOrder=Beans.get(SaleOrderRepository.class).find(saleOrder.getId());
        Invoice invoice = 
        		Beans.get(SaleOrderAccountService.class).generateInvoiceFromSaleOrder(saleOrder);

        response.setView(ActionView.define(I18n.get("Invoice"))
                .model(Invoice.class.getName())
                .add("grid", "invoice-grid")
                .add("form", "invoice-form")
                .domain("self.id = " + invoice.getId())
                .context("_showRecord", invoice.getId())
                .map());
    }
}
