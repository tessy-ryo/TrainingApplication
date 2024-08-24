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
	//部位一件取得
	public BodyParts findOneBodyPart(int bodyPartId);
	
	//**部位複数件取得*/
	public List<BodyParts> findBodyParts();
	
	//筋トレ種目と種目ID、筋トレ部位を一件取得
	public ExerciseRecord getExerciseBodyPartById(int exerciseId);

	/**筋トレ種目取得*/
	public List<Exercise> getExercisesByBodyPart(int bodyPartId);
	
	//重量あり筋トレ種目複数件取得
	public List<Exercise> getWeightBasedExercisesByBodyPart(int bodyPartId);
	
	//**重量の有無の確認*/
	public int findWeightBased(int exerciseId);
	
	//**筋トレの記録*/
	public int recordExerciseData(ExerciseRecord record);
	
	//筋トレデータ取得
	public List<ExerciseRecord> findExerciseData(@Param("userId")Integer userId,
			@Param("searchName")String searchName,
			@Param("offset") Integer offset,
			@Param("size") Integer size);
	
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
	
	//筋トレ種目を追加
	public int insertOne(@Param("name") String name,
			@Param("bodyPartId") Integer BodyPartId,
			@Param("weightBased") Integer weightBased);
	
	//ユーザーの筋トレデータのレコード数をカウントする
	public int countExerciseData(@Param("userId") Integer userId,
			@Param("searchName") String searchName);
	
	//特定の種目の、今までの最大重量を取得する
	public int findMaxWeightByExerciseId(Integer exerciseId);
	
	//特定の種目の、直近7日間の最大重量を取得
	public List<ExerciseRecord> findMaxWeightForLast7Days(Integer exerciseId, int size, int offset);
	
	//特定の種目の、筋トレが記録された日付け（重複無し）の総数をカウントする
	public int countMaxWeightRecords(Integer exerciseId);
}
