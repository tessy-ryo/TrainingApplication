package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.WeightService;
import com.example.form.BodyWeightDataForm;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/training")
public class BodyWeightEditController {
	@Autowired WeightService weightService;
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}
	
	//体重データを削除する画面へ遷移
	@GetMapping("/weight/delete/{id}")
	public String getDelete(@ModelAttribute BodyWeightDataForm form, Authentication authentication, Model model,
			HttpSession session,@PathVariable("id") Integer id) {
		setupModel(model, authentication);
		
		//特定の体重データを取得
		WeightRecord record = weightService.showSpecificBodyWeight(id);
		
		form.setDate(record.getDate());
		
		form.setBodyWeight(record.getBodyWeight());
		
		form.setId(record.getId());
		
		//セッションにフォームデータを保存
		session.setAttribute("bodyWeightDataForm", form);
		
		return  "training/weight/edit/deleteBodyWeight";
	}
	
	//体重データ（１件）削除
	@PostMapping("/weight/edit/deleteBodyWeight")
	public String postDeleteBodyWeight(Authentication authentication, HttpSession session, Model model) {
		setupModel(model, authentication);
		
		BodyWeightDataForm form = (BodyWeightDataForm)session.getAttribute("bodyWeightDataForm");
		
		//体重データ削除
		weightService.deleteBodyWeightDataOne(form.getId());
		
		session.removeAttribute("bodyWeightDataForm");
		
		//体重記録画面にリダイレクト
		return "redirect:/training/weight/record/weightHistory";
	}
	
	//体重データを編集する画面へ遷移
	@GetMapping("/weight/edit/{id}")
	public String getEdit(@ModelAttribute BodyWeightDataForm form, Authentication authentication, Model model,
			HttpSession session,@PathVariable("id") Integer id) {
		//特定の体重データを取得
		WeightRecord record = weightService.showSpecificBodyWeight(id);
				
		form.setDate(record.getDate());
				
		form.setBodyWeight(record.getBodyWeight());
				
		form.setId(record.getId());
				
		//セッションにフォームデータを保存
		session.setAttribute("bodyWeightDataForm", form);
		
		return "training/weight/edit/editBodyWeight";
	}
	
	@PostMapping("/weight/edit/editBodyWeight")
	public String postEditBodyWeight(@ModelAttribute @Validated BodyWeightDataForm form,BindingResult bindingResult,
			HttpSession session, Authentication authentication, Model model) {
		//フォームデータを取り出してsessionFormに格納
		BodyWeightDataForm sessionForm = (BodyWeightDataForm)session.getAttribute("bodyWeightDataForm");
		
		if (bindingResult.hasErrors()) {
			return getEdit(form, authentication,model,session,sessionForm.getId());
		}
	}
}
