package com.example.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.domain.model.Exercise;
import com.example.domain.service.ExerciseService;
import com.example.repository.ExerciseMapper;

@Service
public class ExerciseServiceImpl implements ExerciseService {
	@Autowired ExerciseMapper mapper;
	
	/**筋トレ種目複数件取得*/
	@Override
	public List<Exercise> getExercises(int bodyPartId){
		return mapper.getExercisesByBodyPart(bodyPartId);
	}
}
