package com.example.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.domain.model.MUser;
import com.example.domain.service.UserService;
import com.example.repository.UserMapper;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	private UserMapper mapper;
	
	@Autowired
	private PasswordEncoder encoder;
	
	/**ユーザー登録*/
	@Override
	public void signup(MUser user) {
		//パスワード暗号化
		String rawPassword = user.getPassword();
		user.setPassword(encoder.encode(rawPassword));
		// isAdmin フィールドを 0 に設定
		user.setIsAdmin(0);
		
		mapper.insertOne(user);
	}
	
	@Override
	public MUser getLoginUser(String accountName) {
		return mapper.findLoginUser(accountName);
	}

}
