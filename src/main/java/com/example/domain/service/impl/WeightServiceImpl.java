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
	public int getMinBodyWeight() {
		return mapper.findMinBodyWeight();
	}
	
	//今までの最大重量を取得
	public int getMaxBodyWeight() {
		return mapper.findMaxBodyWeight();
	}
	
	//直近7日間の体重を取得
	public List<WeightRecord> getBodyWeightForLast7Days(int size, int offset) {
		return mapper.findBodyWeightForLast7Days(size, offset);
	}
	
	//体重が記録された日付の総数をカウントする
		public int getCountBodyWeightRecords() {
			return mapper.countBodyWeightRecords();
		}

}
