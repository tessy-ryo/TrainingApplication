package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.form.DashBoardForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/training")
public class DashBoardController {
	@Autowired
	private ExerciseService exerciseService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication,HttpServletRequest request) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
		model.addAttribute("currentUri",request.getRequestURI());
	}

	/**ダッシュボード画面を表示*/
	@GetMapping("/dashboard")
	public String getTrainingDashBoard(@ModelAttribute DashBoardForm form,
			@RequestParam(value="page",defaultValue="1") int page,
			@RequestParam(value="size",defaultValue="6") int size,
			Model model,HttpSession session,Authentication authentication,HttpServletRequest request) {
		//ダッシュボード画面を表示
		setupModel(model,authentication,request);
		
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		form.setUserId(userDetails.getId());
		
		int offset = (page - 1) * size;
		
		List<ExerciseRecord> trainingList = exerciseService.showExerciseData(form.getUserId(),form.getSearchName(),offset,size);
		
		//ユーザーの筋トレデータレコード数をカウント
		int totalRecords = exerciseService.getTotalRecords(form.getUserId(),form.getSearchName());
		
		int totalPages = 0;
		
		if (totalRecords == 0) {
			totalPages = 1;
		}else {
			//レコード数をsizeで割って、合計ページを計算する
			totalPages = (int)Math.ceil((double)totalRecords / size);
		}
			
		model.addAttribute("trainingList",trainingList);
		
		model.addAttribute("currentPage",page);
		
		model.addAttribute("totalPages", totalPages);
		
		return "training/dashboard";
	}
	
	@PostMapping("/dashboard")
	public String postTrainingDashBoard(@ModelAttribute DashBoardForm form,
			@RequestParam(value="page",defaultValue="1") int page,
			@RequestParam(value="size",defaultValue="6") int size,
			Model model,Authentication authentication,HttpServletRequest request) {
		//ダッシュボード画面を表示
		setupModel(model,authentication, request);
		
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		form.setUserId(userDetails.getId());
		
		int offset = (page - 1) * size;
		
		List<ExerciseRecord> trainingList = exerciseService.showExerciseData(form.getUserId(),form.getSearchName(),offset,size);
		
		//ユーザーの筋トレデータレコード数をカウント
		Integer totalRecords = exerciseService.getTotalRecords(form.getUserId(),form.getSearchName());

		int totalPages = 0;
		
		if (totalRecords == null||totalRecords == 0) {
			totalPages = 1;
		}else {
			//レコード数をsizeで割って、合計ページを計算する
			totalPages = (int)Math.ceil((double)totalRecords / size);
		}
				
		model.addAttribute("trainingList",trainingList);
		
		model.addAttribute("currentPage",page);
		
		model.addAttribute("totalPages", totalPages);
		
		return "training/dashboard";
	}
	
}
