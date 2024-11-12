package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.WeightService;
import com.example.form.BodyWeightDataForm;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/weight")
public class WeightEditController {
	@Autowired WeightService weightService;
	
	//体重データを削除する画面へ遷移
	@GetMapping("/delete/{id}")
	public String getDelete(@ModelAttribute BodyWeightDataForm form, Authentication authentication, Model model) {
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		//特定の体重データを取得
		WeightRecord record = weightService.findWeightRecordById(form.getId());
		
		// 現在のユーザーがこのデータにアクセスできるか確認
	    if (record.getUserId()!=(userDetails.getId())) {
	        throw new AccessDeniedException("不正なアクセスです");
	    }
		
		form.setDate(record.getDate());
		
		form.setBodyWeight(record.getBodyWeight());
		
		form.setId(record.getId());
		
		return  "training/weight/edit/deleteBodyWeight";
	}
	
	//体重データ（１件）削除
	@PostMapping("/delete/{id}")
	public String postDeleteBodyWeight(@ModelAttribute BodyWeightDataForm form,Authentication authentication, HttpSession session, Model model) {
		
		//体重データ削除
		weightService.deleteBodyWeightDataOne(form.getId());
		
		//体重記録画面にリダイレクト
		return "redirect:/weight";
	}
	
	//体重データを編集する画面へ遷移
	@GetMapping("/edit/{id}")
	public String getEdit(@ModelAttribute BodyWeightDataForm form, Authentication authentication, Model model) {
		
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		//特定の体重データを取得
		WeightRecord record = weightService.findWeightRecordById(form.getId());
		// 現在のユーザーがこのデータにアクセスできるか確認
	    if (record.getUserId()!=(userDetails.getId())) {
	        throw new AccessDeniedException("不正なアクセスです");
	    }
				
		form.setDate(record.getDate());
				
		form.setBodyWeight(record.getBodyWeight());
				
		form.setId(record.getId());
		
		return "training/weight/edit/editBodyWeight";
	}
	
	//体重データ編集確認画面へ遷移
	@PostMapping("/edit/{id}")
	public String postEditBodyWeight(@ModelAttribute @Validated BodyWeightDataForm form,BindingResult bindingResult,
			Authentication authentication,Model model) {
		
		if (bindingResult.hasErrors()) {
			//NG:体重データを編集する画面に戻る
			return getEdit(form, authentication,model);
		}
		
		return String.format("redirect:/weight/check/editWeight/%d?date=%s&bodyWeight=%s", 
                form.getId(), form.getDate(), form.getBodyWeight());
	}
	
	//体重データ編集確認画面を表示
	@GetMapping("/check/editWeight/{id}")
	public String getEditBodyWeightCheck(
	        Model model,
	        Authentication authentication,
	        @ModelAttribute BodyWeightDataForm form) { 
	    
	    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	    
	    // 特定の体重データを取得し、アクセス確認
	    WeightRecord record = weightService.findWeightRecordById(form.getId());
	    if (record.getUserId() != userDetails.getId()) {
	        throw new AccessDeniedException("不正なアクセスです");
	    }
	    
	    form.setDate(form.getDate());
	    form.setBodyWeight(form.getBodyWeight());
	    
	    model.addAttribute("bodyWeightDataForm", form);
	    
	    return "training/weight/edit/editBodyWeightCheck";
	}

	
	//体重データを編集して体重記録画面へ遷移
	@PostMapping("/check/editWeight/{id}")
	public String postEditBodyWeightCheck(@ModelAttribute BodyWeightDataForm form,Model model, Authentication authentication) {
		
		weightService.updateBodyWeightDataOne(form.getDate(),form.getBodyWeight(),form.getId());
		
		return "redirect:/weight";
	}
}
