package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;
import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.form.ExerciseDataForm;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/training")
public class TrainingEditDeleteController {
	@Autowired
	private ExerciseService exerciseService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
			private void setupModel(Model model,Authentication authentication) {
				CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
				model.addAttribute("username",userDetails.getAccountName());
			}
			
			//トレーニング種目の編集画面を表示するメソッド
			@GetMapping("/edit/{id}")
			public String getEdit(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model, HttpSession session,@PathVariable("id") Integer id) {
				setupModel(model, authentication);
				//特定の筋トレデータ取得
				ExerciseRecord record = exerciseService.showSpecificData(id);
				
				//部位を取得
				List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
				model.addAttribute("bodyPartsList",bodyPartsList);
				
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(record.getBodyPartId());
				model.addAttribute("exerciseList",exerciseList);
				
				//日付け設定
				form.setDate(record.getDate());
				//部位設定
				form.setBodyPartId(record.getBodyPartId());
				//種目設定
				form.setExerciseId(record.getExerciseId());
				//重量設定
				form.setReps(record.getReps());
				//回数設定
				form.setWeight(record.getWeight());
				//レコードの値設定
				form.setId(record.getId());
				
				return "training/exercise/edit";
			}
			
			@PostMapping("/edit")
			public String postEdit(@ModelAttribute ExerciseDataForm form,HttpSession session) {
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm",form);
				
				int weightBased = exerciseService.checkWeightBased(form.getExerciseId());
				
				if(weightBased==0) {
					//重量がない場合
					return "redirect:/training/exercise/editReps";
				}else {
					//重量がある場合
					return "redirect:/training/exercise/editWeightReps";
				}
			}
			
			//回数を編集する画面を表示
			@GetMapping("/exercise/editReps")
			public String getEditReps(Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				model.addAttribute("exerciseDataForm",form);
				
				return "training/exercise/editReps";
			}
			
			@PostMapping("/exercise/editReps")
			public String postEditReps(@ModelAttribute ExerciseDataForm form, Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				//筋トレデータを更新
				exerciseService.updateExerciseRecordOne(sessionForm.getDate(), sessionForm.getBodyPartId(), sessionForm.getExerciseId(), form.getWeight(), form.getReps(),sessionForm.getId());
				
				return "redirect:/training/exercise/editRepsSuccess";
				
			}
			
			//重量と回数を編集する画面を表示
			@GetMapping("/exercise/editWeightReps")
			public String getEditWeightReps(Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				model.addAttribute("exerciseDataForm",form);
				
				return "training/exercise/editWeightReps";
			}
			
			@PostMapping("/exercise/editWeightReps")
			public String postEditWeightReps(@ModelAttribute ExerciseDataForm form, Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				//筋トレデータを更新
				exerciseService.updateExerciseRecordOne(sessionForm.getDate(), sessionForm.getBodyPartId(), sessionForm.getExerciseId(), form.getWeight(), form.getReps(),sessionForm.getId());
				
				return "redirect:/training/exercise/editWeightRepsSuccess";
				
			}
			
			@GetMapping("/exercise/editRepsSuccess")
			public String getEditRepsSuccess(Model model,HttpSession session, Authentication authentication) {
				setupModel(model,authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				ExerciseRecord record = exerciseService.showSpecificData(sessionForm.getId()); 
				
				sessionForm.setReps(record.getReps());
				
				sessionForm.setBodyPartName(record.getBodyParts().getName());
				
				sessionForm.setExerciseName(record.getExercise().getName());
				
				model.addAttribute("form",sessionForm);
				
				session.removeAttribute("exerciseDataForm");
				
				return "training/exercise/editRepsSuccess";
			}
			
			@GetMapping("/exercise/editWeightRepsSuccess")
			public String getEditWeightRepsSuccess(Model model,HttpSession session, Authentication authentication) {
				setupModel(model,authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				ExerciseRecord record = exerciseService.showSpecificData(sessionForm.getId()); 
				
				sessionForm.setWeight(record.getWeight());
				
				sessionForm.setReps(record.getReps());
				
				sessionForm.setBodyPartName(record.getBodyParts().getName());
				
				sessionForm.setExerciseName(record.getExercise().getName());
				
				model.addAttribute("form",sessionForm);
				
				session.removeAttribute("exerciseDataForm");
				
				return "training/exercise/editWeightRepsSuccess";
			}
			
			//トレーニングデータを削除する画面を表示
			@GetMapping("/delete/{id}")
			public String getDelete(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model,HttpSession session,@PathVariable("id") Integer id) {
				setupModel(model,authentication);
				
				//特定の筋トレデータ取得
				ExerciseRecord record = exerciseService.showSpecificData(id);
				
				int weightBased = exerciseService.checkWeightBased(record.getExerciseId());
				
				form.setDate(record.getDate());
				
				form.setBodyPartName(record.getBodyParts().getName());
				
				form.setExerciseName(record.getExercise().getName());
				
				form.setWeight(record.getWeight());
				
				form.setReps(record.getReps());
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm",form);
				
				if(weightBased==0) {
					//重量がない場合
					return "redirect:/training/exercise/deleteReps";
				}else {
					//重量がある場合
					return "redirect:/training/exercise/deleteWeightReps";
				}
			}
			
			//重量ありの削除画面
			@GetMapping("/exercise/deleteWeightReps")
			public String deleteWeightReps(Authentication authentication,HttpSession session,Model model) {
				setupModel(model,authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				model.addAttribute("form",form);
				
				return "training/exercise/deleteWeightReps";
			}
}
