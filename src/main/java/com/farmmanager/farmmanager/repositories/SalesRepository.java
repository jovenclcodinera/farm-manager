package com.farmmanager.farmmanager.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.farmmanager.farmmanager.models.Product;
import com.farmmanager.farmmanager.models.Sale;
import com.farmmanager.farmmanager.models.Status;

public interface SalesRepository extends JpaRepository<Sale, Long> {
	
	@Query("SELECT s from Sale s WHERE s.product = :product AND s.buyer = :buyer AND s.status NOT IN :statusList")
	Sale findByProductAndBuyerNotFinished(@Param("product") Product product, @Param("buyer") String buyer, List<Status> statusList);
	
	@Query("SELECT s from Sale s WHERE s.product = :product AND s.status = 'COMPLETED'")
	List<Sale> getCompletedSalesOfProduct(@Param("product") Product product);
	
	long countByStatus(Status status);
	
	@Query("SELECT s.buyer FROM Sale s")
	Set<String> getAllBuyers();
	
	
	@Query("SELECT s FROM Sale s WHERE s.status = 'COMPLETED' AND year(s.updated_at) = :year")
	List<Sale> getCompletedSalesByYear(@Param("year") Integer year);
	
	List<Sale> findByProduct(Product product);
	
	@Query("SELECT s FROM Sale s WHERE s.deleted_at IS NULL")
	List<Sale> findAllNotDeleted();
}
