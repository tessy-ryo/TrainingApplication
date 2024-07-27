package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.form.ExerciseDataForm;

@Controller
@RequestMapping("/training")
public class DashBoardController {
	@Autowired
	private ExerciseService exerciseService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}

	/**ダッシュボード画面を表示*/
	@GetMapping("/dashboard")
	public String getTrainingDashBoard(@ModelAttribute ExerciseDataForm form,Model model,Authentication authentication) {
		//ダッシュボード画面を表示
		setupModel(model,authentication);
		
		List<ExerciseRecord> trainingList = exerciseService.showExerciseData(form.getSearchName());
		model.addAttribute("trainingList",trainingList);
		
		return "training/dashboard";
	}
	
}
