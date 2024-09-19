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
	
	/**体重を記録*/
	@Override
	public void recordWeight(WeightRecord record, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		record.setUserId(userDetails.getId());//ユーザーID
		mapper.insertWeightRecord(record);
	}
	
	//今までの最低体重を取得
	public Double getMinBodyWeight(int userId) {
		return mapper.findMinBodyWeight(userId);
	}
	
	//今までの最大重量を取得
	public Double getMaxBodyWeight(int userId) {
		return mapper.findMaxBodyWeight(userId);
	}
	
	//直近7日間の体重を取得
	public List<WeightRecord> getBodyWeightForLast7Days(int userId,int size, int offset) {
		return mapper.findBodyWeightForLast7Days(userId,size, offset);
	}
	
	//体重が記録された日付の総数をカウントする
		public int getCountBodyWeightRecords(int userId) {
			return mapper.countBodyWeightRecords(userId);
		}

}
