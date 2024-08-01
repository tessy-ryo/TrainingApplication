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
public class TrainingRecordController {
	@Autowired
	private ExerciseService exerciseService;
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
				List<Exercise> exerciseList = exerciseService.getExercises(record.getBodyParts().getId());
				model.addAttribute("exerciseList",exerciseList);
				
				//日付け設定
				form.setDate(record.getDate());
				//部位設定
				form.setBodyPartId(record.getBodyParts().getId());
				//種目設定
				form.setExerciseId(record.getExercise().getId());
				//重量設定
				form.setReps(record.getReps());
				//回数設定
				form.setWeight(record.getWeight());
				//レコードの値設定
				form.setId(record.getId());
				
				return "training/exercise/edit";
			}
			
			@PostMapping("edit")
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
				
				//編集した重量と回数をsessionに保存
				sessionForm.setWeight(form.getWeight());
				sessionForm.setReps(form.getReps());
			}
			
			//重量と回数を編集する画面を表示
			@GetMapping("/exercise/editWeightReps")
			public String getEditWeightReps(Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				model.addAttribute("exerciseDataForm",form);
				
				return "training/exercise/editWeightReps";
			}
}
