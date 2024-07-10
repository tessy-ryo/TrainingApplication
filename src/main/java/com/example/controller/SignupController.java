package com.example.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.MUser;
import com.example.domain.service.UserService;
import com.example.form.GroupOrder;
import com.example.form.SignupForm;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/user")
@Slf4j
public class SignupController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper modelMapper;

	/**新規登録画面を表示*/
	@GetMapping("/signup")
	public String getSignup(@ModelAttribute SignupForm form) {
		return "user/signup";
	}
	
	/**新規登録処理*/
	@PostMapping("/signup")
	public String postSignup(@ModelAttribute @Validated(GroupOrder.class) SignupForm form,
			BindingResult bindingResult) {
		
		//入力チェック結果
		if(bindingResult.hasErrors()) {
			//NG：新規登録画面に戻る
			return getSignup(form);
		}
		log.info(form.toString());
		
		MUser user = modelMapper.map(form,MUser.class);
		
		userService.signup(user);
		
		//ログイン画面にリダイレクト
		return "redirect:/login";
	}
}
