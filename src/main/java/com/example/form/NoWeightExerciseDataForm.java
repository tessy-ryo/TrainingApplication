package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NoWeightExerciseDataForm {
	private Integer id;
	
	private Integer bodyPartId;
	
	private Integer exerciseId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	private Integer weight;
	
	@NotNull
	private Integer reps;
}
