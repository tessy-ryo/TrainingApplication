package com.example.repository;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;
import com.example.domain.model.ExerciseRecord;

@Mapper
public interface ExerciseMapper {
	//**部位取得*/
	public List<BodyParts> findBodyParts();
	
	//筋トレ種目と種目ID、筋トレ部位を一件取得
	public ExerciseRecord getExerciseBodyPartById(int exerciseId);

	/**筋トレ種目取得*/
	public List<Exercise> getExercisesByBodyPart(int bodyPartId);
	
	//**重量の有無の確認*/
	public int findWeightBased(int exerciseId);
	
	//**筋トレの記録*/
	public int recordExerciseData(ExerciseRecord record);
	
	//筋トレデータ取得
	public List<ExerciseRecord> findExerciseData(ExerciseRecord record);
	
	//特定の筋トレデータ取得
	public ExerciseRecord getSpecificData(int id);
	
	//筋トレデータ更新（1件）
	public void updateOne(@Param("date")Date date,
			@Param("bodyPartId")Integer bodyPartId,
			@Param("exerciseId")Integer exerciseId,
			@Param("weight")Integer weight,
			@Param("reps")Integer reps,
			@Param("id")Integer id);
	
	//筋トレデータ削除（１件）
	public int deleteOne(@Param("id") Integer id);
	
	//筋トレ種目を論理削除する 
	public int softDeleteOne(int id);
}
