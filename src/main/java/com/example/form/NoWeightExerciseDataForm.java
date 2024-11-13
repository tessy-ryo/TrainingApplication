package com.example.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoWeightExerciseDataForm {
	private Integer id;
	
	private Integer bodyPartId;
	
	private Integer exerciseId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate date;
	
	private Double weight;
	
	@NotNull
	private Integer reps;
}
