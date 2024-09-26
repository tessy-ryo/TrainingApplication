package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
		session.setAttribute("exerciseDataForm", form);
		
		return  "";
	}
}
