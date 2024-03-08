package com.axelor.dashboard.module;

import com.axelor.app.AxelorModule;
import com.axelor.dashboard.service.HomeDashboardService;
import com.axelor.dashboard.service.HomeDashboardServiceImpl;

public class DashboardModule extends AxelorModule {

	@Override
	public void configure() {
		bind(HomeDashboardService.class).to(HomeDashboardServiceImpl.class);
	}
	
}
