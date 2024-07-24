package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;

@Mapper
public interface ExerciseMapper {
	//**部位取得*/
	public List<BodyParts> findBodyParts();

	/**筋トレ種目取得*/
	public List<Exercise> getExercisesByBodyPart(int bodyPartId);
	
	//**重量の有無の確認*/
	public int findWeightBased(int id);
}
