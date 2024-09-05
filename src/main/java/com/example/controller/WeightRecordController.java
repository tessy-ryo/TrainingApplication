package com.example.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.WeightService;
import com.example.form.RecordBodyWeightForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/training")
public class WeightRecordController {
	@Autowired
	private WeightService weightService;
	
	@Autowired
	private ModelMapper modelMapper;
	
		//**認証されたユーザーのアカウントネームを表示するメソッド*/
		private void setupModel(Model model,Authentication authentication) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			model.addAttribute("username",userDetails.getAccountName());
		}
		
		//**体重を記録する画面を表示*/
		@GetMapping("/record/weight")
		public String getRecordWeight(@ModelAttribute RecordBodyWeightForm form,Model model,Authentication authentication,HttpSession session) {
			setupModel(model,authentication);
			//体重を記録する画面を表示
			return "training/weight/record/recordWeight";
		}
		
		//**体重記録確認画面に移動*/
		@PostMapping("/record/weight")
		public String postRecordWeight(@Valid @ModelAttribute RecordBodyWeightForm form,BindingResult bindingResult, Model model,HttpSession session, Authentication authentication) {
			setupModel(model,authentication);
			
			if(bindingResult.hasErrors()) {
				model.addAttribute("recordBodyWeightForm",form);
				
				return "training/weight/record/recordWeight";
			}
			//セッションにフォームデータを保存
			session.setAttribute("recordBodyWeightForm", form);
			
			return "redirect:/training/weight/checkWeightRecord";
		}
		
		//**体重記録確認画面を表示*/
		@GetMapping("/weight/checkWeightRecord")
			public String checkWeightRecord(Model model, HttpSession session, Authentication authentication) {
			setupModel(model,authentication);
			//セッションからフォームデータを取得
			RecordBodyWeightForm form = (RecordBodyWeightForm) session.getAttribute("recordBodyWeightForm");
			
			model.addAttribute("recordBodyWeightForm", form);
			
			return "training/weight/record/checkWeightRecord";
		}
		
		@PostMapping("/weight/checkWeightRecord")
			public String confirmWeightRecord(Model model, HttpSession session, Authentication authentication) {
			setupModel(model,authentication);
			
			//セッションからフォームデータを取得
			RecordBodyWeightForm form = (RecordBodyWeightForm) session.getAttribute("recordBodyWeightForm");
			
			WeightRecord record = modelMapper.map(form,WeightRecord.class);
			//体重を記録
			weightService.recordWeight(record,authentication);
			
			//セッションのフォームデータを破棄
			session.removeAttribute("recordBodyWeightForm");
			
			return "redirect:/training/dashboard";
		}
			
		
}
