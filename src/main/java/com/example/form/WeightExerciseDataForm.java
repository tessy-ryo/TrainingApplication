package com.example.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WeightExerciseDataForm {
	private Integer id;
	
	private Integer bodyPartId;
	
	private Integer exerciseId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	@NotNull
	private Double weight;
	
	@NotNull
	private Integer reps;
	
	private String bodyPartName;
	
	private String exerciseName;
}

