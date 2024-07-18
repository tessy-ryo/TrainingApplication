package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.model.WeightRecord;

@Mapper
public interface WeightMapper {
	
	/**体重記録*/
	public int insertWeightRecord(WeightRecord record);
}
