package com.farmmanager.farmmanager.models;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Sale extends EntitySuperClass {

	@ManyToOne(fetch = FetchType.EAGER)
	private Product product;
	@NotNull(message = "Quantity should not be empty")
	@Min(value = 0)
	private Long quantity;
	@NotBlank(message = "Buyer should not be empty")
	private String buyer;
	@Enumerated(EnumType.STRING)
	private Status status;
}
