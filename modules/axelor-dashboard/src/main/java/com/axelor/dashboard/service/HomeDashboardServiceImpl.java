package com.axelor.dashboard.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Query;
import com.axelor.db.JPA;

public class HomeDashboardServiceImpl implements HomeDashboardService {

    @Override
    public List<Map<String, Object>> sumExTaxAmountPerInvoiceDate(Long customerId, LocalDateTime StartDate, LocalDateTime endDate) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        
        System.out.println("Impl called!");

        Query query = JPA.em().createQuery(
                "SELECT SUM(o.exTaxTotal), o.invoiceDateT " +
                        "FROM Invoice AS o " +
                        "WHERE o.statusSelect != 0 " +
                        "AND o.customer.id = :customerId " +
                        "AND o.invoiceDateT BETWEEN :startDate AND :endDate " +
                        "GROUP BY o.invoiceDateT"
        );
        
        
        query.setParameter("customerId", customerId);
        System.err.println(customerId);
        query.setParameter("startDate", StartDate);
        query.setParameter("endDate", endDate); 
        System.err.println(StartDate +" "+ endDate);
        
		List<Object[]> list = query.getResultList();
		
		if(list.size() != 0) {
			System.out.println("List elements: empty"+list);
			}else {
				System.out.println("List elements: "+list);

			}
		list.forEach(System.out::println);

        list.forEach(element->{
        	Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("exTaxAmount", element[0]);
            dataMap.put("invoiceDate", element[1]);
            dataList.add(dataMap);
        });   
        
        dataList.forEach(System.out::println);

        return dataList;
    }
}
