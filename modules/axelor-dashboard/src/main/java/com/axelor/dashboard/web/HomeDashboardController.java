package com.axelor.dashboard.web;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import com.axelor.dashboard.service.HomeDashboardService;
import com.axelor.inject.Beans;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.axelor.rpc.Context;

public class HomeDashboardController {

	public void sumExTaxAmountPerInvoiceDate(ActionRequest request, ActionResponse response) {
		Context context = request.getContext();
	
		@SuppressWarnings("unchecked")
		LinkedHashMap<String, Object> customer = (LinkedHashMap<String, Object>) context.get("customer");
		
		if (customer == null) {
			System.out.println("No customer found");
			return;
		}
		else {
			System.out.println("Customer found!");
		}
		
		LocalDateTime StartDate = LocalDate.parse(request.getContext().get("startDate").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
		LocalDateTime endDate = StartDate.plusMonths(3);

		Long customerId = ((Integer) customer.get("id")).longValue();
		
		response.setData(Beans.get(HomeDashboardService.class).sumExTaxAmountPerInvoiceDate(customerId, StartDate, endDate));
	}
}
