package com.example.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.WeightService;
import com.example.form.RecordWeightForm;

@Controller
@RequestMapping("/training")
public class TrainingRecordController {
	@Autowired
	private WeightService weightService;
	
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
		
		//**体重を記録する画面を表示*/
		@GetMapping("/record/weight")
		public String getRecordWeight(Model model,@ModelAttribute RecordWeightForm form, Authentication authentication) {
			setupModel(model,authentication);
			//体重を記録する画面を表示
			return "training/recordWeight";
		}
		
		//**体重記録処理*/
		@PostMapping("/record/weight")
		public String postRecordWeight(@ModelAttribute RecordWeightForm form,Authentication authentication) {
			WeightRecord record = modelMapper.map(form,WeightRecord.class);
			//体重を記録
			weightService.recordWeight(record,authentication);
			
			return "redirect:/training/record";
		}
}
