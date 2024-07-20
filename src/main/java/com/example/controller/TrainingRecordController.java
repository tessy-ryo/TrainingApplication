package com.example.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.service.CustomUserDetails;

@Controller
@RequestMapping("/training")
public class TrainingRecordController {
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
			private void setupModel(Model model,Authentication authentication) {
				CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
				model.addAttribute("username",userDetails.getAccountName());
			}
			
			//**トレーニング記録画面を表示*/
			@GetMapping("/record")
			public String getTrainingRecord(Model model,Authentication authentication) {
				setupModel(model,authentication);
				//トレーニング記録画面を表示
				return "training/record";
			}
}
