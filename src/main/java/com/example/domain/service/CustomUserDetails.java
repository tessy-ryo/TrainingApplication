package com.example.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.domain.model.MUser;

public class CustomUserDetails implements UserDetails{
	private String accountName;
	private String password;
	private boolean isAdmin;
	private boolean isAccountNonExpired;
	private boolean isAccountNonLocked;
	private boolean isCredentialsNonExpired;
	private boolean isEnabled;
	
	public CustomUserDetails(MUser user) {
		this.accountName = user.getAccountName();
		this.password = user.getPassword();
		this.isAdmin = "1".equals(user.getIsAdmin());
		this.isAccountNonExpired = true;
		this.isAccountNonLocked = true;
		this.isCredentialsNonExpired = true;
		this.isEnabled = true;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO 自動生成されたメソッド・スタブ
		 List<GrantedAuthority> authorities = new ArrayList<>();

		    if (isAdmin) {
		        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
		    } else {
		        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		    }

		    return authorities;
	}

	@Override
	public String getPassword() {
		// TODO 自動生成されたメソッド・スタブ
		return password;
	}

	@Override
	public String getUsername() {
		// TODO 自動生成されたメソッド・スタブ
		return accountName;
	}

	@Override
	public boolean isAccountNonExpired() {
		// TODO 自動生成されたメソッド・スタブ
		return isAccountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		// TODO 自動生成されたメソッド・スタブ
		return isAccountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// TODO 自動生成されたメソッド・スタブ
		return isCredentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		// TODO 自動生成されたメソッド・スタブ
		return isEnabled;
	}

}
