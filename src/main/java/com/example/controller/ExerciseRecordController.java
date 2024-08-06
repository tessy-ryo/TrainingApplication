package com.example.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
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
public class ExerciseRecordController {
	@Autowired
	private ExerciseService exerciseService;
	
	@Autowired
	private ModelMapper modelMapper;
	
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
	
	//**種目を選択する画面を表示*/
	@GetMapping("/exercise/selectExercise")
	public String getSelectExercise(@ModelAttribute ExerciseDataForm form,Model model,Authentication authentication) {
		setupModel(model,authentication);
		
		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList",bodyPartsList);
		//種目を選択する画面を表示
		return "training/exercise/selectExercise";
	}
	
	@PostMapping("/exercise/selectExercise")
	public String postSelectExercise(@ModelAttribute ExerciseDataForm form,Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		//セッションにフォームデータを保存
		session.setAttribute("exerciseDataForm",form);
		
		int weightBased = exerciseService.checkWeightBased(form.getExerciseId());
		
		if(weightBased==0) {
			//重量がない場合
			return "redirect:/training/exercise/recordReps";
		}else {
			//重量がある場合
			return "redirect:/training/exercise/recordWeightReps";
		}          
	}
	
	@GetMapping("/exercise/recordReps")
	public String getRecordReps(Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
		model.addAttribute("exerciseDataForm",form);
		
		return "training/exercise/recordReps";
	}
	
	@PostMapping("/exercise/recordReps")
	public String postRecordReps(@ModelAttribute ExerciseDataForm form,Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
		
		sessionForm.setReps(form.getReps());
		
		ExerciseRecord record = modelMapper.map(sessionForm, ExerciseRecord.class);
		
		exerciseService.recordExercise(record,authentication);
		
		session.removeAttribute("exerciseDataForm");
		
		return "redirect:/training/dashboard";
	}
	
	@GetMapping("/exercise/recordWeightReps")
	public String getRecordWeightReps(Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
		model.addAttribute("exerciseDataForm",form);
		
		return "training/exercise/recordWeightReps";
	}
	
	@PostMapping("/exercise/recordWeightReps")
	public String postRecordWeightReps(@ModelAttribute ExerciseDataForm form, Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		sessionForm.setWeight(form.getWeight());
		
		sessionForm.setReps(form.getReps());
		
		ExerciseRecord record = modelMapper.map(sessionForm, ExerciseRecord.class);
		
		exerciseService.recordExercise(record,authentication);
		
		session.removeAttribute("exerciseDataForm");
		
		return "redirect:/training/dashboard";
	}
}
