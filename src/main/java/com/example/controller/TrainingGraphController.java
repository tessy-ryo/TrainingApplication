package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.BodyParts;
import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.form.ExerciseDataForm;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/training")
public class TrainingGraphController {
	@Autowired
	private ExerciseService exerciseService;
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}
		
	@GetMapping("/selectGraph")
	public String getSelectGraph(Model model, Authentication authentication) {
		setupModel(model,authentication);
		//筋トレグラフ選択画面を表示
		return "training/selectGraph";
	}
	
	//種目グラフ選択画面を表示
	@GetMapping("/exercise/graph/selectExerciseGraph")
	public String getExerciseGraph(@ModelAttribute ExerciseDataForm form, Model model, Authentication authentication) {
		setupModel(model,authentication);
		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList",bodyPartsList);
		
		//種目グラフ選択画面を表示
		return "training/exercise/graph/selectExerciseGraph";
	}
	
	//トレーニンググラフを表示する画面に遷移
	@PostMapping("/exercise/graph/selectExerciseGraph")
	public String postSelectExerciseGraph(@ModelAttribute ExerciseDataForm form, Model model, Authentication authentication,HttpSession session) {
		setupModel(model,authentication);
		
		//セッションにフォームデータを保存
		session.setAttribute("exerciseDataForm",form);
		
		return "redirect:/training/exercise/graph/showTrainingGraph";
	}
	
	//トレーニンググラフを表示する画面を表示
	@GetMapping("/exercise/graph/showTrainingGraph")
	public String getShowTrainingGraph(Model model,Authentication authentication,HttpSession session) {
		setupModel(model,authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());
		
		//特定の種目の、今までの最大重量を取得する
		int maxWeight = exerciseService.getMaxWeightByExerciseId(sessionForm.getExerciseId());
		
		model.addAttribute("exerciseName",record.getExercise().getName());
		
		model.addAttribute("maxWeight",maxWeight);
		
		return "training/exercise/graph/showTrainingGraph";
	}
}
