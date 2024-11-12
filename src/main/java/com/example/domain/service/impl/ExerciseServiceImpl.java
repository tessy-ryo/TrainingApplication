package com.example.domain.service.impl;

import java.time.LocalDate;
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
	
	//部位一件取得
		public BodyParts getOneBodyPart(int bodyPartId) {
			return mapper.findOneBodyPart(bodyPartId);
		}
	
	//部位複数件取得
	@Override
	public List<BodyParts> getBodyParts(){
		return mapper.findBodyParts();
	}
	
	//筋トレ種目と種目ID、筋トレ部位を一件取得
	@Override
	public ExerciseRecord getOneExercise(int exerciseId) {
		return mapper.getExerciseBodyPartById(exerciseId);
	}
	
	//筋トレ種目複数件取得
	@Override
	public List<Exercise> getExercises(int bodyPartId, int userId){
		return mapper.getExercisesByBodyPart(bodyPartId, userId);
	}
	
	//重量あり筋トレ種目複数件取得
	@Override
	public List<Exercise> getWeightBasedExercises(int bodyPartId,int userId){
		return mapper.getWeightBasedExercisesByBodyPart(bodyPartId, userId);
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
	public List<ExerciseRecord> showExerciseData(Integer userId, 
			String searchName,
			Integer offset,
			Integer size){
		return mapper.findExerciseData(userId,searchName,offset,size);
	}
	
	//特定の筋トレデータ取得
	@Override
	public ExerciseRecord showSpecificData(int data) {
		
		return mapper.getSpecificData(data);
	}
	
	//筋トレデータ更新（１件）
	@Override
	public void updateExerciseRecordOne(LocalDate date,
			Integer bodyPartId,
			Integer exerciseId,
			Integer weight,
			Integer reps,
			Integer id) {
		mapper.updateOne(date,bodyPartId,exerciseId,weight,reps,id);
	}
	
	//筋トレデータ削除（１件）
	@Override
	public void deleteExerciseRecordOne(Integer id) {
		mapper.deleteOne(id);
	}
	
	//筋トレ種目を論理削除 
	@Override
	public void softDeleteExercise(int id) {
		mapper.softDeleteOne(id);
	}
	
	//筋トレ種目を追加
	@Override
	public void addExercise(String name,
			Integer bodyPartId,
			Integer userId,
			Integer weightBased) {
		mapper.insertOne(name, bodyPartId, userId, weightBased);
	}
	
	//ユーザーの筋トレデータレコード数をカウント
	@Override
	public int getTotalRecords(Integer userId, String searchName) {
		return mapper.countExerciseData(userId, searchName);
	}
	
	//特定の種目の、今までの最大重量を取得する
	@Override
	public Integer getMaxWeightByExerciseId(Integer exerciseId,Integer userId) {
		return mapper.findMaxWeightByExerciseId(exerciseId, userId);
	}
	
	//特定の種目の、直近7日間の最大重量を取得
	@Override
	public List<ExerciseRecord> getMaxWeightForLast7Days(Integer exerciseId, Integer userId, int size, int offset){
		return mapper.findMaxWeightForLast7Days(exerciseId,userId, size, offset);
	}
		
	//特定の種目の、筋トレが記録された日付け（重複無し）の総数をカウントする
	@Override
	public int getMaxWeightRecords(Integer exerciseId, Integer userId){
		return mapper.countMaxWeightRecords(exerciseId, userId);
	}
	
	//ユーザーが筋トレした日付け（重複無し）をすべて取得する
	@Override
		public List<ExerciseRecord> getAllDistinctTrainingDate(Integer userId){
			return mapper.findAllDistinctTrainingDate(userId);
		}
	
	//種目IDからUserIdを取得する
	@Override
	public ExerciseRecord getUserIdByExerciseId(Integer exerciseId) {
		return mapper.findUserIdByExerciseId(exerciseId);
	}
}
