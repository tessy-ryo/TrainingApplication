package com.example.form;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class RecordWeightForm {
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date date;

	private Double weight;
}
