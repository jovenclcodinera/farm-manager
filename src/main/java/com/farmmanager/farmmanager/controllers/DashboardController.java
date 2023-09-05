package com.farmmanager.farmmanager.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.farmmanager.farmmanager.models.Status;
import com.farmmanager.farmmanager.repositories.ProductsRepository;
import com.farmmanager.farmmanager.repositories.SalesRepository;
import com.farmmanager.farmmanager.services.DashboardService;

@Controller
@PreAuthorize("isAuthenticated()")
public class DashboardController {
	
	@Autowired
	private DashboardService dashboardService;
	
	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private SalesRepository salesRepository;

	@GetMapping("/dashboard")
	public ModelAndView index() {
		Map<String, Object> viewObjectsMap = new HashMap<>();
		viewObjectsMap.put("viewName", "dashboard");
		viewObjectsMap.put("fragmentName", "dashboard");
		viewObjectsMap.put("title", "Dashboard");
		viewObjectsMap.put("contentHeaderTitle", "Dashboard");
		viewObjectsMap.put("scriptViewName", "dashboard");
		viewObjectsMap.put("scriptFragmentName", "dashboard-scripts");
		viewObjectsMap.put("products", dashboardService.getAllProductsWithSales());
		viewObjectsMap.put("availableProducts", productsRepository.findAllNotDeleted().size());
		viewObjectsMap.put("totalSaleOrders", salesRepository.findAllNotDeleted().size());
		viewObjectsMap.put("totalSoldProducts", salesRepository.countByStatus(Status.COMPLETED));
		viewObjectsMap.put("totalBuyers", salesRepository.getAllBuyers().size());
		viewObjectsMap.put("annualRevenues", dashboardService.getAnnualRevenues());
		
        ModelAndView mv = new ModelAndView("index");
		mv.addAllObjects(viewObjectsMap);
        
        return mv;
	}
}
