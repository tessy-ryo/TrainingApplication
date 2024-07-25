package com.example.domain.model;

import java.util.Date;

import lombok.Data;

@Data
public class ExerciseRecord {
	private Integer bodyParts;
	
	private Integer userId;
	
	private Integer exerciseId;
	
	private Date date;
	
	private Integer weight;
	
	private Integer reps;
}
