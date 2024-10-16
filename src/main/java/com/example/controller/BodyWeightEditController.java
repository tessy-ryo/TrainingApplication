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
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		//特定の体重データを取得
		WeightRecord record = weightService.showSpecificBodyWeight(id);
		
		// 現在のユーザーがこのデータにアクセスできるか確認
	    if (record.getUserId()!=(userDetails.getId())) {
	        throw new AccessDeniedException("不正なアクセスです");
	    }
		
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
		setupModel(model, authentication);
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		//特定の体重データを取得
		WeightRecord record = weightService.showSpecificBodyWeight(id);
		// 現在のユーザーがこのデータにアクセスできるか確認
	    if (record.getUserId()!=(userDetails.getId())) {
	        throw new AccessDeniedException("不正なアクセスです");
	    }
				
		form.setDate(record.getDate());
				
		form.setBodyWeight(record.getBodyWeight());
				
		form.setId(record.getId());
				
		//セッションにフォームデータを保存
		session.setAttribute("bodyWeightDataForm", form);
		
		return "training/weight/edit/editBodyWeight";
	}
	
	//体重データ編集確認画面へ遷移
	@PostMapping("/weight/edit/editBodyWeight")
	public String postEditBodyWeight(@ModelAttribute @Validated BodyWeightDataForm form,BindingResult bindingResult,
			HttpSession session, Authentication authentication, Model model) {
		//フォームデータを取り出してsessionFormに格納
		BodyWeightDataForm sessionForm = (BodyWeightDataForm)session.getAttribute("bodyWeightDataForm");
		
		if (bindingResult.hasErrors()) {
			//NG:体重データを編集する画面に戻る
			return getEdit(form, authentication,model,session,sessionForm.getId());
		}
		//日付設定
		sessionForm.setDate(form.getDate());
		//体重設定
		sessionForm.setBodyWeight(form.getBodyWeight());
		//もう一度セッションに変更点を保存
		session.setAttribute("bodyWeightDataForm", sessionForm);
		
		return "redirect:/training/weight/edit/editBodyWeightCheck";
	}
	
	//体重データ編集確認画面を表示
	@GetMapping("/weight/edit/editBodyWeightCheck")
	public String getEditBodyWeightCheck(Model model, Authentication authentication, HttpSession session) {
		setupModel(model, authentication);
		
		//保存されたフォームの取り出し
		BodyWeightDataForm sessionForm = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");
		
		model.addAttribute("bodyWeightDataForm",sessionForm);
		
		return "training/weight/edit/editBodyWeightCheck";
	}
	
	//体重データを編集して体重記録画面へ遷移
	@PostMapping("/weight/edit/editBodyWeightCheck")
	public String postEditBodyWeightCheck(Model model, Authentication authentication, HttpSession session) {
		setupModel(model, authentication);
		
		//保存されたフォームの取り出し
		BodyWeightDataForm sessionForm = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");
		
		weightService.updateBodyWeightDataOne(sessionForm.getDate(),sessionForm.getBodyWeight(),sessionForm.getId());
		
		session.removeAttribute("bodyWeightDataForm");
		
		return "redirect:/training/weight/record/weightHistory";
	}
}
