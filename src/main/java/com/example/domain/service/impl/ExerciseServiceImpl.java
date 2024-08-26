package com.example.domain.service.impl;

import java.util.Date;
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
	public List<Exercise> getExercises(int bodyPartId){
		return mapper.getExercisesByBodyPart(bodyPartId);
	}
	
	//重量あり筋トレ種目複数件取得
	@Override
	public List<Exercise> getWeightBasedExercises(int bodyPartId){
		return mapper.getWeightBasedExercisesByBodyPart(bodyPartId);
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
	public void updateExerciseRecordOne(Date date,
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
	public void softDeleteExercise(int id) {
		mapper.softDeleteOne(id);
	}
	
	//筋トレ種目を追加
	public void addExercise(String name,
			Integer bodyPartId,
			Integer weightBased) {
		mapper.insertOne(name, bodyPartId, weightBased);
	}
	
	//ユーザーの筋トレデータレコード数をカウント
	public int getTotalRecords(Integer userId, String searchName) {
		return mapper.countExerciseData(userId, searchName);
	}
	
	//特定の種目の、今までの最大重量を取得する
	public int getMaxWeightByExerciseId(Integer exerciseId) {
		return mapper.findMaxWeightByExerciseId(exerciseId);
	}
	
	//特定の種目の、直近7日間の最大重量を取得
	public List<ExerciseRecord> getMaxWeightForLast7Days(Integer exerciseId, int size, int offset){
		return mapper.findMaxWeightForLast7Days(exerciseId, size, offset);
	}
		
	//特定の種目の、筋トレが記録された日付け（重複無し）の総数をカウントする
	public int getMaxWeightRecords(Integer exerciseId){
		return mapper.countMaxWeightRecords(exerciseId);
	}
	
	//ユーザーが筋トレした日付け（重複無し）をすべて取得する
		public List<ExerciseRecord> getAllTrainingDate(Integer userId){
			return mapper.findAllTrainingDate(userId);
		}
}
