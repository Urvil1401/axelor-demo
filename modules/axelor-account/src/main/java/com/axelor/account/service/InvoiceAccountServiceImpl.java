package com.axelor.account.service;

import com.axelor.account.db.Move;
import com.axelor.account.db.MoveLine;
import com.axelor.account.db.repo.MoveLineRepository;
import com.axelor.account.db.repo.MoveRepository;
import com.axelor.inject.Beans;
import com.axelor.invoice.db.Invoice;
import com.axelor.invoice.db.InvoiceLine;
import com.google.inject.persist.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class InvoiceAccountServiceImpl implements InvoiceAccountService {
	   
    @Override
    @Transactional
    public Move generateMoveFromInvoice(Invoice invoice) {
    	
        Move move = new Move();
        move.setInvoice(invoice);
        move.setOperationDate(LocalDate.now());
        
        List<MoveLine> moveLines = new ArrayList<>();
        BigDecimal totalInTaxTotal = BigDecimal.ZERO;
        for (InvoiceLine invoiceLine : invoice.getInvoiceLineList()) {
            MoveLine moveLine = new MoveLine();
            moveLine.setMove(move);
            moveLine.setAccount(invoiceLine.getProduct().getAccount());
            moveLine.setCredit(invoiceLine.getInTaxTotal());
            moveLine.setDebit(BigDecimal.ZERO);
            moveLines.add(moveLine);
            Beans.get(MoveLineRepository.class).save(moveLine);
            totalInTaxTotal = totalInTaxTotal.add(invoiceLine.getInTaxTotal());
        }

        MoveLine debitMoveLine = new MoveLine();
        debitMoveLine.setMove(move);
        debitMoveLine.setAccount(invoice.getCustomer().getAccount());
        debitMoveLine.setDebit(totalInTaxTotal);
        debitMoveLine.setCredit(BigDecimal.ZERO);
        Beans.get(MoveLineRepository.class).save(debitMoveLine);
        moveLines.add(debitMoveLine);
        
        move.setMoveLineList(moveLines);
        try {
            return Beans.get(MoveRepository.class).save(move);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while generating the move: " + e.getMessage(), e);
        }
    }
}
