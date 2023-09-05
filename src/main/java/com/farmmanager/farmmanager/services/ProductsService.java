package com.farmmanager.farmmanager.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.farmmanager.farmmanager.models.Product;
import com.farmmanager.farmmanager.models.Sale;
import com.farmmanager.farmmanager.models.Status;
import com.farmmanager.farmmanager.repositories.SalesRepository;

@Service
public class ProductsService {

    @Autowired
    private SalesRepository salesRepository;

    public void updateSalesOfDeletedProduct(Product product) {
        List<Sale> sales = salesRepository.findByProduct(product).stream().filter(sale -> (sale.getStatus() != Status.CANCELLED || sale.getStatus() != Status.COMPLETED)).collect(Collectors.toList());
        if (! sales.isEmpty()) {
            sales.forEach(sale -> {
                sale.setStatus(Status.CANCELLED);
                salesRepository.save(sale);
            });
        }
    }
}
