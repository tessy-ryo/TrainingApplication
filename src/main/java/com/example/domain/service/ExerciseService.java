package com.example.domain.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;
import com.example.domain.model.ExerciseRecord;

public interface ExerciseService {
	//部位複数件取得
	public List<BodyParts> getBodyParts();
	
	//筋トレ種目複数件取得
	public List<Exercise> getExercises(int bodyPartId);

	//重量の有無の確認
	public int checkWeightBased(int exerciseId);
	
	//筋トレ記録(重量無し）
	public int recordReps(ExerciseRecord record,Authentication authentication);
	
	//筋トレ記録（重量あり）
	public int recordWeightReps(ExerciseRecord record,Authentication authentication);
}
