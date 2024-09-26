package com.example.form;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ExerciseDataForm {
	private Integer id;
	
	@NotNull
	private Integer bodyPartId;
	
	@NotNull
	private Integer exerciseId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private LocalDate date;
	
	private Integer weight;
	
	private Integer reps;
	
	private String bodyPartName;
	
	private String exerciseName;
}
