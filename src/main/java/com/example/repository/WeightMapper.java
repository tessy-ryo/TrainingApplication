package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.model.WeightRecord;

@Mapper
public interface WeightMapper {
	//日付と体重を取得
	public List<WeightRecord> findBodyWeight(int userId, String searchName, int size, int offset);
	
	//特定の日付と体重を取得
	public WeightRecord findSpecificBodyWeight(int id);
	
	//ユーザーの体重データのレコード数を検索を含めてカウントする
	public int countBodyWeightData(int userId, String searchName);
	
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
}
