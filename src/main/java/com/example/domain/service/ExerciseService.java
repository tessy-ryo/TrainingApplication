package com.example.domain.service;

import java.util.List;

import com.example.domain.model.Exercise;

public interface ExerciseService {
	
	public List<Exercise> getExercises(int bodyPartId);

}
