package com.example.form;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TrainingGraphForm {
	@NotNull
	private Integer bodyPartId;
	
	@NotNull
	private Integer exerciseId;
}
