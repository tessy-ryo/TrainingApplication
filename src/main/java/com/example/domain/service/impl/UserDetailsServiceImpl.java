package com.example.domain.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.domain.model.MUser;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.UserService;

@Service
public class UserDetailsServiceImpl implements UserDetailsService{
	
	@Autowired
	private UserService service;
	
	@Override
	public UserDetails loadUserByUsername(String accountName)
		throws UsernameNotFoundException{
		//ユーザー情報取得
		MUser loginUser = service.getLoginUser(accountName);
		
		//ユーザーが存在しない場合
		if(loginUser == null) {
			throw new UsernameNotFoundException("user not found");
		}
		
		CustomUserDetails userDetails = (CustomUserDetails) new CustomUserDetails(loginUser);
		
		return userDetails;
		
	}

}
