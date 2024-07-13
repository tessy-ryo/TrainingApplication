package com.example.domain.service;

import com.example.domain.model.MUser;

public interface UserService {

	/**ユーザー登録*/
	public void signup(MUser user);
	
	/**ログインユーザー情報取得*/
	public MUser getLoginUser(String accountName);
}
