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
                "SELECT SUM(i.exTaxTotal), i.invoiceDateT " +
                        "FROM Invoice AS i " +
                        "WHERE i.statusSelect != 0 " +
                        "AND i.customer.id = :customerId " +
                        "AND i.invoiceDateT BETWEEN :startDate AND :endDate " +
                        "GROUP BY i.invoiceDateT"
        );
        
        
        query.setParameter("customerId", customerId);
        query.setParameter("startDate", StartDate);
        query.setParameter("endDate", endDate); 
        
		List<Object[]> list = query.getResultList();

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
