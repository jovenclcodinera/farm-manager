package com.farmmanager.farmmanager.models;

import java.time.LocalDate;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public class EntitySuperClass {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@CreatedDate
	private Date created_at;
	@LastModifiedDate
	private Date updated_at;
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private User createdBy;
	@ManyToOne(fetch = FetchType.LAZY)
	@JsonBackReference
	private User updatedBy;
	@Column(nullable = true)
	@DateTimeFormat
	private LocalDate deleted_at;
}
