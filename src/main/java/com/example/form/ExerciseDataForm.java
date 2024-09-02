package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class ExerciseDataForm {
	private Integer id;
	
	@NotNull
	private Integer bodyPartId;
	
	
	private Integer userId;
	
	@NotNull
	private Integer exerciseId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date date;
	
	private Integer weight;
	
	private Integer reps;
	
	private String searchName;
	
	private String bodyPartName;
	
	private String exerciseName;
	
	private Integer weightBased;
	
	private String weightBasedText;
}
