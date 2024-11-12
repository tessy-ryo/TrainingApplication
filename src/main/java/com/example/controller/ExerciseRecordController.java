package com.example.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.modelmapper.ModelMapper;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
@RequestMapping("/exercise")
public class ExerciseRecordController {
	@Autowired
	private ExerciseService exerciseService;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@ModelAttribute
	public void checkExerciseAccess(@ModelAttribute ExerciseDataForm form,Authentication authentication) {
		if(form.getExerciseId() != null) {
			CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
			ExerciseRecord record = exerciseService.getUserIdByExerciseId(form.getExerciseId());
			
			// recordがnullでない場合のみアクセス権限をチェック
            if (record != null && !record.getUserId().equals(userDetails.getId())) {
                throw new AccessDeniedException("不正なアクセスです");
            }
		}
	}


	//筋トレ記録画面を表示
	@GetMapping("")
	public String getTrainingDashBoard(@ModelAttribute HistoryForm form,
			@RequestParam(value="page",defaultValue="1") int page,
			@RequestParam(value="size",defaultValue="6") int size,
			Model model,HttpSession session,Authentication authentication,HttpServletRequest request) {
		//ダッシュボード画面を表示
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
	
	//種目を選択する画面を表示
	@GetMapping("/add/event")
	public String getSelectExercise(@ModelAttribute ExerciseDataForm form,Model model,Authentication authentication) {
		
		//現在の日付をフォームのdateフィールドにセット
		form.setDate(LocalDate.now());
		
		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList",bodyPartsList);
		
		//種目を選択する画面を表示
		return "training/exercise/record/selectExercise";
	}
	
	@PostMapping("/add/event")
	public String postSelectExercise(@ModelAttribute @Validated ExerciseDataForm form,BindingResult bindingResult,Model model,Authentication authentication) {
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("exerciseDataForm",form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList",bodyPartsList);
			//部位は選択されていた場合
			if(form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId(),userDetails.getId());
				model.addAttribute("exerciseList",exerciseList);
			}
			return "training/exercise/record/selectExercise";
		}
		
		int weightBased = exerciseService.checkWeightBased(form.getExerciseId());
		
		if(weightBased==0) {
			//重量がない場合
			return String.format("redirect:/exercise/add/reps?date=%s&bodyPartId=%d&exerciseId=%d", form.getDate(),form.getBodyPartId(),form.getExerciseId());
		}else {
			//重量がある場合
			return String.format("redirect:/exercise/add/weight?date=%s&bodyPartId=%d&exerciseId=%d", form.getDate(),form.getBodyPartId(),form.getExerciseId());
		}          
	}
	
	//回数を記録する画面に遷移
	@GetMapping("/add/reps")
	public String getRecordReps(@ModelAttribute NoWeightExerciseDataForm form,Model model) {
		
		return "training/exercise/record/recordReps";
	}
	
	@PostMapping("/add/reps")
	public String postRecordReps(@ModelAttribute @Validated NoWeightExerciseDataForm form,BindingResult bindingResult,Model model,RedirectAttributes redirectAttributes) {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("noWeightExerciseDataForm",form);
			
			return "training/exercise/record/recordReps";
		}
		
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
		redirectAttributes.addAttribute("reps", form.getReps());
		
		return "redirect:/exercise/check/reps";
	}
	
	@GetMapping("/check/reps")
	public String getRecordRepsCheck(@ModelAttribute ExerciseDataForm form,Model model) {
		
		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(form.getExerciseId());
		//筋トレ部位を設定
		form.setBodyPartName(record.getBodyParts().getName());
		//筋トレ種目を設定
		form.setExerciseName(record.getExercise().getName());
		
		return "training/exercise/record/recordRepsCheck";
	}
	
	@PostMapping("/check/reps")
	public String postRecordRepsCheck(@ModelAttribute ExerciseDataForm form,Authentication authentication,Model model,RedirectAttributes redirectAttributes) {
		
		ExerciseRecord record = modelMapper.map(form, ExerciseRecord.class);
		
		//筋トレを記録
		exerciseService.recordExercise(record,authentication);
		
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
		
		return "redirect:/exercise/confirm/reps";
	}
	
	//トレーニングデータ記録完了のお知らせ（重量なし）へ遷移
		@GetMapping("/confirm/reps")
		public String getConfirmRecordReps(@ModelAttribute ExerciseDataForm form,Model model, HttpSession session,Authentication authentication) {
			
			return "training/exercise/record/confirmRecordReps";
		}
		
		//回数を記録する画面へ遷移
		@PostMapping("/confirm/reps")
		public String postConfirmRecordReps(@ModelAttribute ExerciseDataForm form,Model model,HttpSession session,Authentication authentication,RedirectAttributes redirectAttributes) {
			
			String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			redirectAttributes.addAttribute("date", formattedDate);
			redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
			redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
			
			return "redirect:/exercise/add/reps";
		}
	
	//重量と回数を記録する画面に遷移
	@GetMapping("/add/weight")
	public String getRecordWeightReps(@ModelAttribute WeightExerciseDataForm form,Model model) {
		
		return "training/exercise/record/recordWeightReps";
	}
	
	@PostMapping("/add/weight")
	public String postRecordWeightReps(@ModelAttribute @Validated WeightExerciseDataForm form, BindingResult bindingResult,Model model, RedirectAttributes redirectAttributes) {
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("weightExerciseDataForm",form);
			
			return "training/exercise/record/recordWeightReps";
		}
		
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
		redirectAttributes.addAttribute("reps", form.getReps());
		redirectAttributes.addAttribute("weight", form.getWeight());
		
		return "redirect:/exercise/check/weight";
	}
	
	@GetMapping("/check/weight")
	public String getRecordWeightRepsCheck(@ModelAttribute ExerciseDataForm form,Model model) {
		
		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(form.getExerciseId());
		//筋トレ部位を設定
		form.setBodyPartName(record.getBodyParts().getName());
		//筋トレ種目を設定
		form.setExerciseName(record.getExercise().getName());
		
		return "training/exercise/record/recordWeightRepsCheck";
	}
	
	@PostMapping("/check/weight")
	public String postRecordWeightRepsCheck(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model,RedirectAttributes redirectAttributes) {
		
		ExerciseRecord record = modelMapper.map(form, ExerciseRecord.class);
		
		//筋トレを記録
		exerciseService.recordExercise(record,authentication);
		
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
		
		return "redirect:/exercise/confirm/weight";
	}
	
	//トレーニングデータ記録完了のお知らせ（重量あり）へ遷移
	@GetMapping("/confirm/weight")
	public String getConfirmRecordWeightReps(@ModelAttribute ExerciseDataForm form,Model model, HttpSession session,Authentication authentication) {
		
		return "training/exercise/record/confirmRecordWeightReps";
	}
	
	//重量と回数を記録する画面へ遷移
	@PostMapping("/confirm/weight")
	public String postConfirmRecordWeightReps(@ModelAttribute ExerciseDataForm form,Model model,HttpSession session,Authentication authentication, RedirectAttributes redirectAttributes) {
		
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
		
		return "redirect:/exercise/add/weight";
	}
}
