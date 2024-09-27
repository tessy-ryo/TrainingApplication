package com.example.domain.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.example.domain.model.WeightRecord;

public interface WeightService {
	//日付と体重を取得
	public List<WeightRecord> getBodyWeight(int userId, String searchName, int size, int offset);
	
	//特定の日付と体重を取得
	public WeightRecord showSpecificBodyWeight(int id);
	
	//ユーザーの体重データのレコード数を検索を含めてカウントする
	public int getCountBodyWeightData(int userId, String searchName);
	
	//筋トレデータ1件削除
	public void deleteBodyWeightDataOne(Integer id);
	
	/**体重を記録*/
	public void recordWeight(WeightRecord record, Authentication authentication);
	
	//今までの最低体重を取得
	public Double getMinBodyWeight(int userId);
	
	//今までの最大体重を取得
	public Double getMaxBodyWeight(int userId);
	
	//直近7日間の体重を取得
	public List<WeightRecord> getBodyWeightForLast7Days(int userId, int size, int offset);
	
	//体重が記録された日付の総数をカウントする
	public int getCountBodyWeightRecords(int userId);
}
