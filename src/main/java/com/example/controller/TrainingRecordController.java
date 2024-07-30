package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.form.ExerciseDataForm;

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
			public String edit(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model, @PathVariable("id") Integer id) {
				setupModel(model, authentication);
				//特定の筋トレデータ取得
				ExerciseRecord record = exerciseService.showSpecificData(id);
				
				form.setDate(record.getDate());
				form.set
			}
}
