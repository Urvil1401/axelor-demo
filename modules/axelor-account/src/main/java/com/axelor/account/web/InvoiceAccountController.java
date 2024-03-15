package com.axelor.account.web;

import com.axelor.account.db.Move;
import com.axelor.account.service.InvoiceAccountService;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class InvoiceAccountController {

    public void generateMoveFromInvoice(ActionRequest request, ActionResponse response) {
    	
        Invoice invoice = request.getContext().asType(Invoice.class);

        InvoiceAccountService accountService = Beans.get(InvoiceAccountService.class);
        Move generatedMove = accountService.generateMoveFromInvoice(invoice);
        response.setView(ActionView.define("Move")
                .model(Move.class.getName())
                .add("form", "move-form")
                .context("_showRecord", generatedMove.getId())
                .map());
        
    }
    
}
