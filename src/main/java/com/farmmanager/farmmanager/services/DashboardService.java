package com.farmmanager.farmmanager.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.money.Monetary;
import javax.money.MonetaryAmount;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.MonetaryConversions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmmanager.farmmanager.models.Product;
import com.farmmanager.farmmanager.models.Sale;
import com.farmmanager.farmmanager.repositories.ProductsRepository;
import com.farmmanager.farmmanager.repositories.SalesRepository;

@Service
public class DashboardService {
	
	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private SalesRepository salesRepository;

	public List<Map<String, Object>> getAllProductsWithSales() {
		List<Map<String, Object>> products = new ArrayList<>();
		productsRepository.findAllNotDeleted().forEach(p -> {
			try {
				Map<String, Object> map = Product.mapParameters(p);
				Map<String, Object> newMap = new HashMap<>();
				newMap.putAll(map);
				newMap.put("sales", salesRepository.getCompletedSalesOfProduct(p).size());
				products.add(newMap);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		});
		
		return products;
	}
	
	public List<Double> getAnnualRevenues() {
		List<Double> monthlyRevenues = new ArrayList<>();
		List<Sale> completedSales = salesRepository.getCompletedSalesByYear(LocalDate.now().getYear());
		for (int i=1; i<=12; i++) {
			int temp = i;
			List<Double> sum = new ArrayList<>();
			List<Sale> salesByMonth = completedSales.stream().filter(sale -> sale.getUpdated_at().getMonth() == temp).collect(Collectors.toList());
			salesByMonth.forEach(sale -> {
				MonetaryAmount monetaryAmount = Monetary.getDefaultAmountFactory().setCurrency(sale.getProduct().getCurrency()).setNumber(sale.getProduct().getPrice()).create();
				Currency currentCurrency = Currency.getInstance(Locale.getDefault());
				CurrencyConversion conversion = MonetaryConversions.getConversion(currentCurrency.getCurrencyCode());
				MonetaryAmount convertedAmount = monetaryAmount.with(conversion);
				sum.add(convertedAmount.getNumber().doubleValue());
			});
			monthlyRevenues.add(sum.stream().collect(Collectors.summingDouble(f -> f)));
		}
		
		return monthlyRevenues;
	}
}
