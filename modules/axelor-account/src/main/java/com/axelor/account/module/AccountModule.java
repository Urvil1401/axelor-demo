package com.axelor.account.module;

import com.axelor.account.db.repo.AccountAccountRepository;
import com.axelor.account.db.repo.AccountRepository;
import com.axelor.account.db.repo.MoveAccountRepository;
import com.axelor.account.db.repo.MoveRepository;
import com.axelor.account.service.InvoiceAccountService;
import com.axelor.account.service.InvoiceAccountServiceImpl;
import com.axelor.account.service.SaleOrderAccountService;
import com.axelor.account.service.SaleOrderAccountServiceImpl;
import com.axelor.app.AxelorModule;

public class AccountModule extends AxelorModule {

	@Override
	public void configure() {
		bind(SaleOrderAccountService.class).to(SaleOrderAccountServiceImpl.class);
		bind(InvoiceAccountService.class).to(InvoiceAccountServiceImpl.class);
		bind(AccountRepository.class).to(AccountAccountRepository.class);
		bind(MoveRepository.class).to(MoveAccountRepository.class);
	}
	
}
