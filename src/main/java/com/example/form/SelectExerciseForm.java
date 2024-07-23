package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class SelectExerciseForm {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;
	
	private Integer bodyParts;
	
	private Integer exercise;
}
