package com.farmmanager.farmmanager.controllers;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.farmmanager.farmmanager.models.Alert;
import com.farmmanager.farmmanager.models.Product;
import com.farmmanager.farmmanager.models.Sale;
import com.farmmanager.farmmanager.models.Status;
import com.farmmanager.farmmanager.repositories.ProductsRepository;
import com.farmmanager.farmmanager.repositories.SalesRepository;
import com.farmmanager.farmmanager.services.SalesService;
import com.farmmanager.farmmanager.utilities.CustomUserDetails;

@Controller
@PreAuthorize("isAuthenticated()")
public class SalesController {
	
	@Autowired
	private SalesRepository salesRepository;
	
	@Autowired
	private ProductsRepository productsRepository;
	
	@Autowired
	private SalesService salesService;
	
	@GetMapping("sales")
	public ModelAndView index() {
		Map<String, Object> viewObjectsMap = new HashMap<>();
		viewObjectsMap.put("viewName", "sales/sales");
		viewObjectsMap.put("fragmentName", "sales-index");
		viewObjectsMap.put("title", "Sales - Index");
		viewObjectsMap.put("contentHeaderTitle", "Sales");
		viewObjectsMap.put("sales", salesService.convertObjectListToMapList(salesRepository.findAllNotDeleted()));
		viewObjectsMap.put("scriptViewName", "sales/sales");
		viewObjectsMap.put("scriptFragmentName", "sale-scripts");
		viewObjectsMap.put("sale", new Sale());
		viewObjectsMap.put("statusList", Stream.of(Status.values()).map(Status::name).collect(Collectors.toList()));
		
		ModelAndView mv = new ModelAndView("index");
		mv.addAllObjects(viewObjectsMap);
        
        return mv;
	}

	@PostMapping("/sales")
	public ModelAndView save(@Valid @ModelAttribute("sale") Sale sale, RedirectAttributes redirectAttributes, @RequestParam(required = true, name = "product") Long productId) {
		Product product = productsRepository.findById(productId).orElse(null);
		if (product == null) {
			redirectAttributes.addFlashAttribute("alert", new Alert("warning", "Product with id: " + productId + " not found"));
			return new ModelAndView("redirect:/products");
		}else if (salesRepository.findByProductAndBuyerNotFinished(product, sale.getBuyer(), SalesService.notFinishedList()) != null) {
			redirectAttributes.addFlashAttribute("alert", new Alert("warning", "Sale for " + product.getName() + " already exists"));
			return new ModelAndView("redirect:/products");
		} else {
			CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			sale.setCreated_at(new Date());
			sale.setUpdated_at(new Date());
			sale.setCreatedBy(user.getUser());
			sale.setUpdatedBy(user.getUser());
			sale.setStatus(Status.CREATED);
			sale.getProduct().setQuantity(sale.getProduct().getQuantity() - sale.getQuantity());
			productsRepository.save(sale.getProduct());
			salesRepository.save(sale);
			redirectAttributes.addFlashAttribute("alert", new Alert("success", "Sale for product: " + product.getName() + " successfully created"));
		}
		
		return new ModelAndView("redirect:/sales");
	}
	
	@PutMapping("/sales/{id}")
	public ModelAndView update(@Valid @ModelAttribute("sale") Sale sale, RedirectAttributes redirectAttributes, @PathVariable("id") Long id) {
		Sale oldSale = salesRepository.findById(id).orElse(null);
		if (oldSale == null) {
			redirectAttributes.addFlashAttribute("alert", new Alert("error", "Sale for product with id: " + id + " does not exist"));
		} else {
			CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (sale.getQuantity() < oldSale.getQuantity()) {
				Long difference = oldSale.getQuantity() - sale.getQuantity();
				oldSale.getProduct().setQuantity(difference);
				productsRepository.save(oldSale.getProduct());
			}
			
			if (sale.getStatus() == Status.CANCELLED) {
				oldSale.getProduct().setQuantity(oldSale.getProduct().getQuantity() + sale.getQuantity());
				productsRepository.save(oldSale.getProduct());
			}
			
			oldSale.setQuantity(sale.getQuantity());
			oldSale.setBuyer(sale.getBuyer());
			oldSale.setStatus(sale.getStatus());
			oldSale.setUpdated_at(new Date());
			oldSale.setUpdatedBy(user.getUser());
			
			salesRepository.save(oldSale);
			redirectAttributes.addFlashAttribute("alert", new Alert("success", "Sale for product: " + oldSale.getProduct().getName() + " successfully updated"));
		}
		
		
		return new ModelAndView("redirect:/sales");
	}
	
	@DeleteMapping("/sales/{id}")
	public ModelAndView destroy(RedirectAttributes redirectAttributes, @PathVariable("id") Long id) {
		Sale sale = salesRepository.findById(id).orElse(null);
		if (sale == null) {
			redirectAttributes.addFlashAttribute("alert", new Alert("error", "Sale for product with id: " + id + " does not exist"));
		} else {
			sale.setDeleted_at(LocalDate.now());
			if (sale.getStatus() != Status.CANCELLED || sale.getStatus() != Status.COMPLETED) {
				sale.getProduct().setQuantity(sale.getProduct().getQuantity() + sale.getQuantity());
				productsRepository.save(sale.getProduct());
				sale.setStatus(Status.CANCELLED);
			}
			salesRepository.save(sale);
			redirectAttributes.addFlashAttribute("alert", new Alert("success", "Sale for product: " + sale.getProduct().getName() + " successfully deleted"));
		}
		
		return new ModelAndView("redirect:/sales");
	}
}
