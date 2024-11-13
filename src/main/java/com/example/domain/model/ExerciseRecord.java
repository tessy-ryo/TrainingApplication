package com.example.domain.model;

import java.time.LocalDate;

import lombok.Data;

//筋トレデータ
@Data
public class ExerciseRecord {
	private Integer id;
	
	private Integer bodyPartId;
	
	private Integer userId;
	
	private Integer exerciseId;
	
	private LocalDate date;
	
	private Double weight;
	
	private Integer reps;
	
	private Exercise exercise;
	
	private BodyParts bodyParts;
}
