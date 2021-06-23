package com.farmmanager.farmmanager.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.farmmanager.farmmanager.models.Category;
import com.farmmanager.farmmanager.models.Product;

public interface ProductsRepository extends JpaRepository<Product, Long> {

	Product findByName(String name);
	Long countByCategory(Category category);
	@Query("SELECT p FROM Product p WHERE p.deleted_at IS NULL")
	List<Product> findAllNotDeleted();
}
