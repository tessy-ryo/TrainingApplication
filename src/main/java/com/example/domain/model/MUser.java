package com.example.domain.model;

import lombok.Data;

@Data
public class MUser {
	private String accountName;
	private String password;
	private String isAdmin;
}
