package com.example.domain.model;

import java.util.Date;

import lombok.Data;

@Data
public class WeightRecord {
	private int userId;
	
	private Date date;

	private Double weight;
}
