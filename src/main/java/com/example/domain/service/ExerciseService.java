package com.example.domain.service;

import java.time.LocalDate;
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
	
	//重量あり筋トレ種目複数件取得
	public List<Exercise> getWeightBasedExercises(int bodyPartId);

	//重量の有無の確認
	public int checkWeightBased(int exerciseId);
	
	//筋トレ記録
	public int recordExercise(ExerciseRecord record,Authentication authentication);
	
	//筋トレデータ取得
	public List<ExerciseRecord> showExerciseData(Integer userId, 
			String searchName,
			Integer offset,
			Integer size);
	
	//特定の筋トレデータ取得
	public ExerciseRecord showSpecificData(int data);
	
	//筋トレデータ更新
	public void updateExerciseRecordOne(LocalDate date,
			Integer bodyPartId,
			Integer exerciseId,
			Integer weight,
			Integer reps,
			Integer id);
	
	//筋トレデータ削除（１件）
	public void deleteExerciseRecordOne(Integer id);
	
	//筋トレ種目を論理削除
	public void softDeleteExercise(int id);
	
	//筋トレ種目を追加
	public void addExercise(String name,
			Integer bodyPartId,
			Integer weightBased);
	
	//ユーザーの筋トレデータレコード数をカウント
	public int getTotalRecords(Integer userId, String searchName);
	
	//特定の種目の、今までの最大重量を取得する
	public Integer getMaxWeightByExerciseId(Integer exerciseId, Integer userId);
	
	//特定の種目の、直近7日間の最大重量を取得
	public List<ExerciseRecord> getMaxWeightForLast7Days(Integer exerciseId, Integer userId,int size, int offset);
	
	//特定の種目の、筋トレが記録された日付け（重複無し）の総数をカウントする
	public int getMaxWeightRecords(Integer exerciseId, Integer userId);
	
	//ユーザーが筋トレした日付け（重複無し）をすべて取得する
	public List<ExerciseRecord> getAllDistinctTrainingDate(Integer userId);
}
