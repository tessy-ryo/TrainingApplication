package com.example.domain.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;
import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.repository.ExerciseMapper;

@Service
public class ExerciseServiceImpl implements ExerciseService {
	@Autowired ExerciseMapper mapper;
	
	//部位複数権取得
	@Override
	public List<BodyParts> getBodyParts(){
		return mapper.findBodyParts();
	}
	
	//筋トレ種目複数件取得
	@Override
	public List<Exercise> getExercises(int bodyPartId){
		return mapper.getExercisesByBodyPart(bodyPartId);
	}
	
	//重量の有無の確認
	@Override
	public int checkWeightBased(int exerciseId) {
		return mapper.findWeightBased(exerciseId);
	}
	
	//筋トレ記録
	@Override
	public int recordExercise(ExerciseRecord record,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		record.setUserId(userDetails.getId());
		return mapper.recordExerciseData(record);
	}
	
	//筋トレデータ取得
	@Override
	public List<ExerciseRecord> showExerciseData(ExerciseRecord record,Authentication authentication){
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		record.setUserId(userDetails.getId());
		
		return mapper.findExerciseData(record);
	}
	
	//特定の筋トレデータ取得
	@Override
	public ExerciseRecord showSpecificData(int data) {
		
		return mapper.getSpecificData(data);
	}
	
	
}
