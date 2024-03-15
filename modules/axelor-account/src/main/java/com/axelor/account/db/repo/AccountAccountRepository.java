package com.axelor.account.db.repo;

import com.axelor.account.db.Account;

public class AccountAccountRepository extends AccountRepository {
   
	@Override
    public Account save(Account entity) {
		int code = entity.getCode();
		String name = entity.getName();
        entity.setFullName(code+ " - " + name);
        return super.save(entity);
        
    }
	
}
