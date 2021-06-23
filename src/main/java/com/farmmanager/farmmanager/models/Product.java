package com.farmmanager.farmmanager.models;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Product extends EntitySuperClass {

	@NotBlank(message = "Name should not be empty")
	private String name;
	@NotNull(message = "Quantity should not be empty")
	@Min(value = 0)
	private long quantity = 0;
	private String quantityUnit = "pcs";
	@NotNull(message = "Price should not be empty")
	@Min(value = 0)
	private double price;
	@NotNull(message = "Currency should not be empty")
	private String currency;
	@Enumerated(EnumType.STRING)
	private Category category;
	
	public static Map<String, Object> mapParameters(Object object) throws IllegalArgumentException, IllegalAccessException {
		Map<String, Object> map = new HashMap<>();
		for (Field field : object.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			map.put(field.getName(), field.get(object));
		}
		
		return map;
	}
}
