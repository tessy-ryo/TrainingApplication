package com.example.repository;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.domain.model.WeightRecord;

@Mapper
public interface WeightMapper {
	//日付と体重を取得
	public List<WeightRecord> findBodyWeight(int userId, String searchName, int size, int offset);
	
	//特定の日付と体重を取得
	public WeightRecord findSpecificBodyWeight(int id);
	
	//ユーザーの体重データのレコード数を検索を含めてカウントする
	public int countBodyWeightData(int userId, String searchName);
	
	//筋トレデータ1件削除
	public int  deleteOneBodyWeightData(@Param("id") Integer id);
	
	/**体重記録*/
	public int insertWeightRecord(WeightRecord record);
	
	//今までの最大体重を取得
	public Double findMinBodyWeight(int userId);
	
	//今までの最大体重を取得
	public Double findMaxBodyWeight(int userId);
	
	//直近7日間の体重を取得
	public List<WeightRecord> findBodyWeightForLast7Days(int userId,int size, int offset);
	
	//体重が記録された日付の総数をカウントする
	public int countBodyWeightRecords(int userId);
	
	//体重データをIDで更新する
	public void updateOneBodyWeightData(@Param("date")LocalDate date,
			@Param("bodyWeight")Double bodyWeight,
			@Param("id")Integer id);
	
	//ユーザーが体重を記録した日付け（重複無し）をすべて取得する
	public List<WeightRecord> findAllDistinctWeightRecordsDate(Integer userId);
}
