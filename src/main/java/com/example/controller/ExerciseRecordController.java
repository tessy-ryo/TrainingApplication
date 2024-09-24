package com.example.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;
import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.form.ExerciseDataForm;
import com.example.form.HistoryForm;
import com.example.form.NoWeightExerciseDataForm;
import com.example.form.WeightExerciseDataForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/training")
public class ExerciseRecordController {
	@Autowired
	private ExerciseService exerciseService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}
			
	/**筋トレ記録画面を表示*/
	@GetMapping("/exercise/record/trainingHistory")
	public String getTrainingDashBoard(@ModelAttribute HistoryForm form,
			@RequestParam(value="page",defaultValue="1") int page,
			@RequestParam(value="size",defaultValue="6") int size,
			Model model,HttpSession session,Authentication authentication,HttpServletRequest request) {
		//ダッシュボード画面を表示
		setupModel(model,authentication);
		
		model.addAttribute("currentUri",request.getRequestURI());
		
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
		
		return "training/exercise/record/trainingHistory";
	}
	
	@PostMapping("/exercise/record/trainingHistory")
	public String postTrainingDashBoard(@ModelAttribute HistoryForm form,
			@RequestParam(value="page",defaultValue="1") int page,
			@RequestParam(value="size",defaultValue="6") int size,
			Model model,Authentication authentication,HttpServletRequest request) {
		//ダッシュボード画面を表示
		setupModel(model,authentication);
		
		model.addAttribute("currentUri",request.getRequestURI());
		
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
		
		return "training/exercise/record/trainingHistory";
	}
	
	//**種目を選択する画面を表示*/
	@GetMapping("/exercise/selectExercise")
	public String getSelectExercise(@ModelAttribute ExerciseDataForm form,Model model,HttpSession session,Authentication authentication) {
		setupModel(model,authentication);
		
		//セッションにフォームデータがある場合、破棄する
		session.removeAttribute("exerciseDataForm");
		
		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList",bodyPartsList);
		
		//種目を選択する画面を表示
		return "training/exercise/record/selectExercise";
	}
	
	@PostMapping("/exercise/selectExercise")
	public String postSelectExercise(@ModelAttribute @Validated ExerciseDataForm form,BindingResult bindingResult,Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("exerciseDataForm",form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList",bodyPartsList);
			//部位は選択されていた場合
			if(form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId());
				model.addAttribute("exerciseList",exerciseList);
			}
			return "training/exercise/record/selectExercise";
		}
		
		//セッションにフォームデータを保存
		session.setAttribute("exerciseDataForm",form);
		
		int weightBased = exerciseService.checkWeightBased(form.getExerciseId());
		
		if(weightBased==0) {
			//重量がない場合
			return "redirect:/training/exercise/recordReps";
		}else {
			//重量がある場合
			return "redirect:/training/exercise/recordWeightReps";
		}          
	}
	
	//回数を記録する画面に遷移
	@GetMapping("/exercise/recordReps")
	public String getRecordReps(Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
		model.addAttribute("noWeightExerciseDataForm",form);
		
		return "training/exercise/record/recordReps";
	}
	
	@PostMapping("/exercise/recordReps")
	public String postRecordReps(@ModelAttribute @Validated NoWeightExerciseDataForm form,BindingResult bindingResult,Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("noWeightExerciseDataForm",form);
			
			return "training/exercise/record/recordReps";
		}
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
		
		sessionForm.setReps(form.getReps());
		
		//もう一度セッションにフォームデータを保存
		session.setAttribute("exerciseDataForm", sessionForm);
		
		return "redirect:/training/exercise/recordRepsCheck";
	}
	
	@GetMapping("/exercise/recordRepsCheck")
	public String getRecordRepsCheck(Authentication authentication,Model model,HttpSession session) {
		setupModel(model, authentication);
		
		//保存されたフォームの取り出し
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());
		//筋トレ部位を設定
		sessionForm.setBodyPartName(record.getBodyParts().getName());
		//筋トレ種目を設定
		sessionForm.setExerciseName(record.getExercise().getName());
		
		model.addAttribute("exerciseDataForm",sessionForm);
		
		return "training/exercise/record/recordRepsCheck";
	}
	
	@PostMapping("/exercise/recordRepsCheck")
	public String postRecordRepsCheck(Authentication authentication,Model model, HttpSession session) {
		setupModel(model, authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		ExerciseRecord record = modelMapper.map(sessionForm, ExerciseRecord.class);
		
		//筋トレを記録
		exerciseService.recordExercise(record,authentication);
		
		return "redirect:/training/exercise/confirmRecordReps";
	}
	
	
	//トレーニングデータ記録完了のお知らせ（重量なし）へ遷移
		@GetMapping("/exercise/confirmRecordReps")
		public String getConfirmRecordReps(Model model, HttpSession session,Authentication authentication) {
			setupModel(model,authentication);
			
			return "training/exercise/record/confirmRecordReps";
		}
		
		//重量と回数を記録する画面へ遷移
		@PostMapping("/exercise/confirmRecordReps")
		public String postConfirmRecordReps(Model model,HttpSession session,Authentication authentication) {
			setupModel(model,authentication);
			
			ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
			//回数をnullに設定
			sessionForm.setReps(null);
			
			return "redirect:/training/exercise/recordReps";
		}
	
	//重量と回数を記録する画面に遷移
	@GetMapping("/exercise/recordWeightReps")
	public String getRecordWeightReps(Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
		model.addAttribute("weightExerciseDataForm",form);
		
		return "training/exercise/record/recordWeightReps";
	}
	
	@PostMapping("/exercise/recordWeightReps")
	public String postRecordWeightReps(@ModelAttribute @Validated WeightExerciseDataForm form, BindingResult bindingResult,Model model,HttpSession session,Authentication authentication) {
		setupModel(model, authentication);
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("weightExerciseDataForm",form);
			
			return "training/exercise/record/recordWeightReps";
		}
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		sessionForm.setWeight(form.getWeight());
		
		sessionForm.setReps(form.getReps());
		
		//日付け、部位のid、種目id、重量、回数すべてをセッションに保存
		session.setAttribute("exerciseDataForm", sessionForm);
		
		return "redirect:/training/exercise/recordWeightRepsCheck";
	}
	
	@GetMapping("/exercise/recordWeightRepsCheck")
	public String getRecordWeightRepsCheck(Authentication authentication,Model model,HttpSession session) {
		setupModel(model,authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());
		//筋トレ部位を設定
		sessionForm.setBodyPartName(record.getBodyParts().getName());
		//筋トレ種目を設定
		sessionForm.setExerciseName(record.getExercise().getName());
		
		model.addAttribute("exerciseDataForm",sessionForm);
		
		return "training/exercise/record/recordWeightRepsCheck";
	}
	
	@PostMapping("/exercise/recordWeightRepsCheck")
	public String postRecordWeightRepsCheck(Authentication authentication,Model model, HttpSession session) {
		setupModel(model, authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		ExerciseRecord record = modelMapper.map(sessionForm, ExerciseRecord.class);
		
		//筋トレを記録
		exerciseService.recordExercise(record,authentication);
		
		return "redirect:/training/exercise/confirmRecordWeightReps";
	}
	
	//トレーニングデータ記録完了のお知らせ（重量あり）へ遷移
	@GetMapping("/exercise/confirmRecordWeightReps")
	public String getConfirmRecordWeightReps(Model model, HttpSession session,Authentication authentication) {
		setupModel(model,authentication);
		
		return "training/exercise/record/confirmRecordWeightReps";
	}
	
	//重量と回数を記録する画面へ遷移
	@PostMapping("/exercise/confirmRecordWeightReps")
	public String postConfirmRecordWeightReps(Model model,HttpSession session,Authentication authentication) {
		setupModel(model,authentication);
		
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		//重量をnullに設定
		sessionForm.setWeight(null);
		//回数をnullに設定
		sessionForm.setReps(null);
		
		return "redirect:/training/exercise/recordWeightReps";
	}
}
