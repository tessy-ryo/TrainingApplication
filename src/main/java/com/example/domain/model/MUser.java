package com.example.domain.model;

import lombok.Data;

@Data
public class MUser {
	private int id;
	
	private String accountName;
	
	private String password;
	
	private int isAdmin;
}
