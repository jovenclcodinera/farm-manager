package com.farmmanager.farmmanager.models;

import java.util.stream.Stream;

public enum Status {
	CREATED("Created"),
	ONGOING("Ongoing"),
	PENDING("Pending"),
	COMPLETED("Completed"),
	CANCELLED("Cancelled");
	
	private String status;

	private Status(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}
	
	public static Status of(String status) {
		return Stream.of(Status.values()).filter(s -> s.getStatus() == status).findFirst().orElseThrow(IllegalArgumentException::new);
	}
}
