package com.example.domain.service.impl;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.WeightService;
import com.example.repository.WeightMapper;

@Service
public class WeightServiceImpl implements WeightService{
	
	@Autowired
	private WeightMapper mapper;
	
	//日付と体重を取得
	@Override
	public List<WeightRecord> getBodyWeight(int userId, String searchName, int size, int offset) {
		return mapper.findBodyWeight(userId, searchName, size, offset);
	}
	
	//特定の日付と体重を取得
	@Override
	public WeightRecord showSpecificBodyWeight(int id) {
		return mapper.findSpecificBodyWeight(id);
	}
	
	/**体重を記録*/
	@Override
	public void recordWeight(WeightRecord record, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		record.setUserId(userDetails.getId());//ユーザーID
		mapper.insertWeightRecord(record);
	}
	
	//ユーザーの体重データのレコード数を検索を含めてカウントする
	@Override
	public int getCountBodyWeightData(int userId, String searchName) {
		return mapper.countBodyWeightData(userId, searchName);
	}
	
	//今までの最低体重を取得
	@Override
	public Double getMinBodyWeight(int userId) {
		return mapper.findMinBodyWeight(userId);
	}
	
	//今までの最大重量を取得
	@Override
	public Double getMaxBodyWeight(int userId) {
		return mapper.findMaxBodyWeight(userId);
	}
	
	//直近7日間の体重を取得
	@Override
	public List<WeightRecord> getBodyWeightForLast7Days(int userId,int size, int offset) {
		return mapper.findBodyWeightForLast7Days(userId,size, offset);
	}
	
	//体重が記録された日付の総数をカウントする
	@Override
		public int getCountBodyWeightRecords(int userId) {
			return mapper.countBodyWeightRecords(userId);
		}

}
