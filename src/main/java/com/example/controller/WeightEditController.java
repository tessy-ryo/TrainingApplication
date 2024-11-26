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
	@Autowired
	WeightService weightService;

	@ModelAttribute
	public void checkWeightAccess(@ModelAttribute BodyWeightDataForm form, Authentication authenticaion, Model model,
			Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//特定の体重データを取得
		WeightRecord record = weightService.findWeightRecordById(form.getId());
		// 現在のユーザーがこのデータにアクセスできるか確認
		if (record.getUserId() != (userDetails.getId())) {
			throw new AccessDeniedException("不正なアクセスです");
		}
	}

	//体重データを削除する画面へ遷移
	@GetMapping("/delete/{id}")
	public String getDelete(@ModelAttribute BodyWeightDataForm form, HttpSession session, Model model) {

		//特定の体重データを取得
		WeightRecord record = weightService.findWeightRecordById(form.getId());

		form.setDate(record.getDate());

		form.setBodyWeight(record.getBodyWeight());

		form.setId(record.getId());

		//セッションにフォームデータを保存
		session.setAttribute("bodyWeightDataForm", form);

		return "training/weight/edit/deleteBodyWeight";
	}

	//体重データ（１件）削除
	@PostMapping("/delete/{id}")
	public String postDeleteBodyWeight(Authentication authentication, HttpSession session, Model model) {

		BodyWeightDataForm form = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");

		//体重データ削除
		weightService.deleteBodyWeightDataOne(form.getId());

		session.removeAttribute("bodyWeightDataForm");

		//体重記録画面にリダイレクト
		return "redirect:/weight";
	}

	//体重データを編集する画面へ遷移
	@GetMapping("/edit/{id}")
	public String getEdit(@ModelAttribute BodyWeightDataForm form, HttpSession session) {

		//特定の体重データを取得
		WeightRecord record = weightService.findWeightRecordById(form.getId());

		form.setDate(record.getDate());

		form.setBodyWeight(record.getBodyWeight());

		form.setId(record.getId());

		//セッションにフォームデータを保存
		session.setAttribute("bodyWeightDataForm", form);

		return "training/weight/edit/editBodyWeight";
	}

	//体重データ編集確認画面へ遷移
	@PostMapping("/edit/{id}")
	public String postEditBodyWeight(@ModelAttribute @Validated BodyWeightDataForm form, BindingResult bindingResult,
			Authentication authentication, Model model, HttpSession session) {

		//フォームデータを取り出してsessionFormに格納
		BodyWeightDataForm sessionForm = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");

		if (bindingResult.hasErrors()) {
			//NG:体重データを編集する画面に戻る
			return getEdit(form, session);
		}
		//日付設定
		sessionForm.setDate(form.getDate());
		//体重設定
		sessionForm.setBodyWeight(form.getBodyWeight());
		//もう一度セッションに変更点を保存
		session.setAttribute("bodyWeightDataForm", sessionForm);

		return "redirect:/weight/check/editWeight/{id}";
	}

	//体重データ編集確認画面を表示
	@GetMapping("/check/editWeight/{id}")
	public String getEditBodyWeightCheck(Model model, HttpSession session) throws Exception{

		//保存されたフォームの取り出し
		BodyWeightDataForm sessionForm = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");

		if (sessionForm == null) {
            throw new Exception("不正な操作が行われました");
        }
		
		model.addAttribute("bodyWeightDataForm", sessionForm);

		return "training/weight/edit/editBodyWeightCheck";
	}

	//体重データを編集して体重記録画面へ遷移
	@PostMapping("/check/editWeight/{id}")
	public String postEditBodyWeightCheck(Model model, HttpSession session) {

		//保存されたフォームの取り出し
		BodyWeightDataForm sessionForm = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");

		weightService.updateBodyWeightDataOne(sessionForm.getDate(), sessionForm.getBodyWeight(), sessionForm.getId());

		session.removeAttribute("bodyWeightDataForm");

		return "redirect:/weight";
	}
}
