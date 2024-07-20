package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.application.service.ExerciseApplicationService;
import com.example.domain.service.CustomUserDetails;
import com.example.form.SelectExerciseForm;

@Controller
@RequestMapping("/training")
public class ExerciseRecordController {
	@Autowired
	private ExerciseApplicationService exerciseApplicationService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}
			
	//**種目を選択する画面を表示*/
	@GetMapping("/exercise/selectExercise")
	public String getSelectExercise(@ModelAttribute SelectExerciseForm form,Model model,Authentication authentication) {
		setupModel(model,authentication);
		
		//部位を取得
		Map<String,Integer> bodyPartsMap = exerciseApplicationService.getBodyPartsMap();
		model.addAttribute("bodyPartsMap",bodyPartsMap);
		//種目を選択する画面を表示
		return "training/exercise/selectExercise";
	}
}
