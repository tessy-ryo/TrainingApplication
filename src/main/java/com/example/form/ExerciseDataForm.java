package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class ExerciseDataForm {
	private Integer id;
	
	private Integer bodyPartId;
	
	private Integer userId;
	
	private Integer exerciseId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	private Integer weight;
	
	private Integer reps;
	
	private String searchName;
}
