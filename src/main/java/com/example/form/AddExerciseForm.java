package com.example.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class AddExerciseForm {
	@NotNull
	private Integer bodyPartId;
	
	@NotBlank
	private String exerciseName;
	
	@NotNull
	private Integer weightBased;
	
	private String weightBasedText;
	
	private String bodyPartName;
}
