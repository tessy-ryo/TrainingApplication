package com.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.service.CustomUserDetails;

@Controller
@RequestMapping("/training")
public class DashBoardController {
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}

	/**ダッシュボード画面を表示*/
	@GetMapping("/dashboard")
	public String getTrainingDashBoard(Model model,Authentication authentication) {
		//ダッシュボード画面を表示
		setupModel(model,authentication);
		return "training/dashboard";
	}
	
}
