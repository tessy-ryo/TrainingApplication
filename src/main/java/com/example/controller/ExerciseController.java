package com.example.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.domain.model.Exercise;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;

@RestController
@RequestMapping("/api")
public class ExerciseController {
	@Autowired
	private ExerciseService exerciseService;
	
	@GetMapping("/exercise")
	public List<Map<String,Object>> listExercises(@RequestParam("bodyPartId") Integer bodyPartId, Model model,Authentication authentication){
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		List<Exercise> exercises = exerciseService.getExercises(bodyPartId,userDetails.getId());
		return exercises.stream()
				.map(exercise -> {
					Map<String, Object> map = new HashMap<>();
					map.put("id", exercise.getId());
					map.put("name", exercise.getName());
					return map;
				})
				.collect(Collectors.toList());
	}
	
	@GetMapping("/exercise/weightBased")
	public List<Map<String,Object>> listWeightBasedExercises(@RequestParam("bodyPartId") Integer bodyPartId ){
		List<Exercise> exercises = exerciseService.getWeightBasedExercises(bodyPartId);
		return exercises.stream()
				.map(exercise -> {
					Map<String, Object> map = new HashMap<>();
					map.put("id", exercise.getId());
					map.put("name", exercise.getName());
					return map;
				})
				.collect(Collectors.toList());
	}
}
