package com.example.domain.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class WeightRecord {
	private int id;
	
	private int userId;
	
	private LocalDate date;

	private Double bodyWeight;
}
