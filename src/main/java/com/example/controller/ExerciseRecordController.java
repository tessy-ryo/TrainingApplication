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
	public String getSelectExercise(@ModelAttribute ExerciseDataForm form,Model model,HttpSession session,Authentication authentication) {
		setupModel(model,authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
		
		if (sessionForm != null) {
			form.setDate(sessionForm.getDate());
		}
		
		session.removeAttribute("exerciseDataForm");
		
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
	
	//回数を記録する画面に遷移
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
		
		return "redirect:/training/exercise/confirmRecordReps";
	}
	
	//トレーニングデータ記録完了のお知らせ（重量なし）へ遷移
		@GetMapping("/exercise/confirmRecordReps")
		public String getConfirmRecordReps(Model model, HttpSession session,Authentication authentication) {
			setupModel(model,authentication);
			
			ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
			
			ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());
			//筋トレ部位を設定
			sessionForm.setBodyPartName(record.getBodyParts().getName());
			//筋トレ種目を設定
			sessionForm.setExerciseName(record.getExercise().getName());
			
			model.addAttribute("exerciseDataForm",sessionForm);
			
			return "training/exercise/confirmRecordReps";
		}
		
		//重量と回数を記録する画面へ遷移
		@PostMapping("/exercise/confirmRecordReps")
		public String postConfirmRecordReps(Model model,HttpSession session,Authentication authentication) {
			setupModel(model,authentication);
			
			ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
			//回数をnullに設定
			sessionForm.setReps(null);
			
			return "redirect:/training/exercise/recordReps";
		}
	
	//重量と回数を記録する画面に遷移
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
		
		//日付け、部位のid、種目id、重量、回数すべてをセッションに保存
		session.setAttribute("exerciseDataForm", sessionForm);
		
		ExerciseRecord record = modelMapper.map(sessionForm, ExerciseRecord.class);
		
		//筋トレを記録
		exerciseService.recordExercise(record,authentication);
		
		return "redirect:/training/exercise/confirmRecordWeightReps";
	}
	
	//トレーニングデータ記録完了のお知らせ（重量あり）へ遷移
	@GetMapping("/exercise/confirmRecordWeightReps")
	public String getConfirmRecordWeightReps(Model model, HttpSession session,Authentication authentication) {
		setupModel(model,authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());
		//筋トレ部位を設定
		sessionForm.setBodyPartName(record.getBodyParts().getName());
		//筋トレ種目を設定
		sessionForm.setExerciseName(record.getExercise().getName());
		
		model.addAttribute("exerciseDataForm",sessionForm);
		
		return "training/exercise/confirmRecordWeightReps";
	}
	
	//重量と回数を記録する画面へ遷移
	@PostMapping("/exercise/confirmRecordWeightReps")
	public String postConfirmRecordWeightReps(Model model,HttpSession session,Authentication authentication) {
		setupModel(model,authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		//重量をnullに設定
		sessionForm.setWeight(null);
		//回数をnullに設定
		sessionForm.setReps(null);
		
		return "redirect:/training/exercise/recordWeightReps";
	}
}
