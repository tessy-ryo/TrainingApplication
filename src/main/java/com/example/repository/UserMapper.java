package com.example.repository;

import org.apache.ibatis.annotations.Mapper;

import com.example.domain.model.MUser;

@Mapper
public interface UserMapper {

	/**ユーザー登録*/
	public int insertOne(MUser user);
}
