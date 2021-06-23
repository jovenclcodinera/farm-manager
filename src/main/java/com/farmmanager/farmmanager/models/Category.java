package com.farmmanager.farmmanager.models;

import java.util.stream.Stream;

public enum Category {
	CROPS("Crops"), 
	POULTRY("Poultry"),
	DAIRY("Dairy"),
	FISHERY("Fishery"),
	FORESTRY("Forestry"),
	HORTICULTURE("Horticulture");
	
	private String category;

	private Category(String category) {
		this.category = category;
	}

	public String getCategory() {
		return category;
	}
	
	public static Category of(String category) {
		return Stream.of(Category.values()).filter(c -> c.getCategory() == category).findFirst().orElseThrow(IllegalArgumentException::new);
	}
}
