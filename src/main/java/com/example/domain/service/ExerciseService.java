package com.example.domain.service;

import java.util.Date;
import java.util.List;

import org.springframework.security.core.Authentication;

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;
import com.example.domain.model.ExerciseRecord;

public interface ExerciseService {
	//部位一件取得
	public BodyParts getOneBodyPart(int bodyPartId);
	
	//部位複数件取得
	public List<BodyParts> getBodyParts();
	
	//筋トレ種目と種目ID、筋トレ部位を一件取得
	public ExerciseRecord getOneExercise(int exerciseId);
	
	//筋トレ種目複数件取得
	public List<Exercise> getExercises(int bodyPartId);

	//重量の有無の確認
	public int checkWeightBased(int exerciseId);
	
	//筋トレ記録
	public int recordExercise(ExerciseRecord record,Authentication authentication);
	
	//筋トレデータ取得
	public List<ExerciseRecord> showExerciseData(ExerciseRecord record,Authentication authentication);
	
	//特定の筋トレデータ取得
	public ExerciseRecord showSpecificData(int data);
	
	//筋トレデータ更新
	public void updateExerciseRecordOne(Date date,
			Integer bodyPartId,
			Integer exerciseId,
			Integer weight,
			Integer reps,
			Integer id);
	
	//筋トレデータ削除（１件）
	public void deleteExerciseRecordOne(Integer id);
	
	//筋トレ種目を論理削除
	public void softDeleteExercise(int id);
}
