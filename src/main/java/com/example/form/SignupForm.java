package com.example.form;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SignupForm {
	
	@NotBlank(groups = ValidGroup1.class)
	private String accountName;
	
	@NotBlank(groups = ValidGroup1.class)
	@Length(min=4,max=100,groups = ValidGroup2.class)
	@Pattern(regexp="^[a-zA-Z0-9]+$",groups = ValidGroup2.class)
	private String password;

}
