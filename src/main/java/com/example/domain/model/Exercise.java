package com.example.domain.model;

import lombok.Data;

//種目
@Data
public class Exercise {
	private Integer id;
	private String name;
	private Integer bodyPartId;
	private Integer weightBased;
}
