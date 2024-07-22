package com.example.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.model.Exercise;

@Mapper
public interface ExerciseMapper {

	/**筋トレ種目取得*/
	public List<Exercise> getExercisesByBodyPart(int bodyPart);
}
