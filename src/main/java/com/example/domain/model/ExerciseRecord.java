package com.example.domain.model;

import java.util.Date;

import lombok.Data;

@Data
public class ExerciseRecord {
	private Integer bodyPartId;
	
	private Integer userId;
	
	private Integer exerciseId;
	
	private Date date;
	
	private Integer weight;
	
	private Integer reps;
	
	private Exercise exercise;
	
	private BodyParts bodyParts;
}
