package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.service.ExerciseService;

@RestController
@RequestMapping("/api")
public class ExerciseController {
	@Autowired
	private ExerciseService exerciseService;
	
	@GetMapping("/exercise")
	public List<Exercise> listExercises(int )
}
