package com.axelor.dashboard.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface HomeDashboardService {

	List<Map<String, Object>> sumExTaxAmountPerInvoiceDate(Long customerId, LocalDateTime StartDate, LocalDateTime endDate);

}
