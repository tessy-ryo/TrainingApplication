package com.example.domain.service;

import org.springframework.security.core.Authentication;

import com.example.domain.model.WeightRecord;

public interface WeightService {

	/**体重を記録*/
	public void recordWeight(WeightRecord record, Authentication authentication);
}
