package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecordBodyWeightForm {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@NotNull
	private Date date;

	@NotNull
	private Double bodyWeight;
}
