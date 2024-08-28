package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExerciseDataFormWithWeight {
	private Integer id;
	
	private Integer bodyPartId;
	
	private Integer userId;
	
	private Integer exerciseId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	@NotNull
	private Integer weight;
	
	@NotNull
	private Integer reps;
	
	private String searchName;
	
	private String bodyPartName;
	
	private String exerciseName;
	
	private Integer weightBased;
	
	private String weightBasedText;
}
