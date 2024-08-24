package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.model.WeightRecord;

@Mapper
public interface WeightMapper {
	
	/**体重記録*/
	public int insertWeightRecord(WeightRecord record);
	
	//今までの最大体重を取得
	public int findMinBodyWeight();
	
	//今までの最大体重を取得
	public int findMaxBodyWeight();
	
	//直近7日間の体重を取得
	public List<WeightRecord> findBodyWeightForLast7Days(int size, int offset);
	
	//体重が記録された日付の総数をカウントする
	public int countBodyWeightRecords();
}
