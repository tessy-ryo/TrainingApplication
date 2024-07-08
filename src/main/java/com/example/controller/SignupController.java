package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class SignupController {

	@GetMapping("/signup")
	public String getSignup() {
		return "user/signup";
	}
	
	@PostMapping("/signup")
	public String postSignup() {
		return "redirect:/login";
	}
}
