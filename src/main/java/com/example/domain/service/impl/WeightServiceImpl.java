package com.example.domain.service.impl;
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

}
