package com.farmmanager.farmmanager.controllers;

import java.time.LocalDate;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmmanager.farmmanager.models.Alert;
import com.farmmanager.farmmanager.models.Category;
import com.farmmanager.farmmanager.models.Product;
import com.farmmanager.farmmanager.models.Sale;
import com.farmmanager.farmmanager.models.Status;
import com.farmmanager.farmmanager.repositories.ProductsRepository;
import com.farmmanager.farmmanager.services.ProductsService;
import com.farmmanager.farmmanager.utilities.CustomUserDetails;

@Controller
@PreAuthorize("isAuthenticated()")
public class ProductsController {

	@Autowired
	private ProductsRepository productRepository;

	@Autowired
	private ProductsService productsService;

	@GetMapping("/products")
	public ModelAndView index() {
		Map<String, Object> viewObjectsMap = new HashMap<>();
		Map<String, Long> categoryCounts = new HashMap<>();
		viewObjectsMap.put("viewName", "products/products");
		viewObjectsMap.put("fragmentName", "products-index");
		viewObjectsMap.put("title", "Products - Index");
		viewObjectsMap.put("contentHeaderTitle", "Products");
		viewObjectsMap.put("scriptViewName", "products/products");
		viewObjectsMap.put("product", new Product());
		viewObjectsMap.put("sale", new Sale());
		viewObjectsMap.put("scriptFragmentName", "product-scripts");
		viewObjectsMap.put("categories", Stream.of(Category.values()).map(Category::name).collect(Collectors.toList()));
		viewObjectsMap.put("products", productRepository.findAllNotDeleted());
		viewObjectsMap.put("statusList", Stream.of(Status.values()).map(Status::name).collect(Collectors.toList()));
		Stream.of(Category.values()).forEach(category -> {
			categoryCounts.put(category.getCategory(), productRepository.countByCategory(category));
		});
		viewObjectsMap.put("categoryCounts", categoryCounts);
		viewObjectsMap.put("currencies", Currency.getAvailableCurrencies());
		
		ModelAndView mv = new ModelAndView("index");
		mv.addAllObjects(viewObjectsMap);
        
        return mv;
	}
	
	@PostMapping("/products")
	public ModelAndView save(@Valid @ModelAttribute("product") Product product, RedirectAttributes redirectAttributes) {
		if (productRepository.findByName(product.getName()) != null) {
			redirectAttributes.addFlashAttribute("alert", new Alert("error", "Product: " + product.getName() + " already exists"));
		} else {
			CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			product.setCreated_at(new Date());
			product.setUpdated_at(new Date());
			product.setCreatedBy(user.getUser());
			product.setUpdatedBy(user.getUser());
			productRepository.save(product);
			redirectAttributes.addFlashAttribute("alert", new Alert("success", "Product: " + product.getName() + " successfully created"));
		}
		
		return new ModelAndView("redirect:/products");
	}
	
	@PutMapping("/products/{id}")
	public ModelAndView update(@Valid @ModelAttribute("product") Product product, RedirectAttributes redirectAttributes, @PathVariable("id") Long id) {
		Product updatedProduct = productRepository.findById(id).orElse(null);
		if (updatedProduct == null) {
			redirectAttributes.addFlashAttribute("alert", new Alert("error", "Product: " + product.getName() + " does not exist"));
		} else {
			CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			updatedProduct.setName(product.getName());
			updatedProduct.setPrice(product.getPrice());
			updatedProduct.setQuantity(product.getQuantity());
			updatedProduct.setQuantityUnit(product.getQuantityUnit());
			updatedProduct.setCurrency(product.getCurrency());
			updatedProduct.setCategory(product.getCategory());
			updatedProduct.setUpdated_at(new Date());
			updatedProduct.setUpdatedBy(user.getUser());
			
			productRepository.save(updatedProduct);
			redirectAttributes.addFlashAttribute("alert", new Alert("success", "Product: " + product.getName() + " successfully updated"));
		}
		
		return new ModelAndView("redirect:/products");
	}
	
	@DeleteMapping("/products/{id}")
	public ModelAndView destroy(RedirectAttributes redirectAttributes, @PathVariable("id") Long id) {
		if (productRepository.findById(id).orElse(null) == null) {
			redirectAttributes.addFlashAttribute("alert", new Alert("error", "Product does not exist"));
		} else {
			Product product = productRepository.getById(id);
			product.setDeleted_at(LocalDate.now());
			product.setUpdated_at(new Date());
			productsService.updateSalesOfDeletedProduct(product);
			productRepository.save(product);
			redirectAttributes.addFlashAttribute("alert", new Alert("success", "Product: " + product.getName() + " successfully deleted"));
		}
		
		return new ModelAndView("redirect:/products");
	}
}
