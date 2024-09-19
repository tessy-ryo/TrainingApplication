package com.example.domain.service;

import java.util.List;

import org.springframework.security.core.Authentication;

import com.example.domain.model.WeightRecord;

public interface WeightService {

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
