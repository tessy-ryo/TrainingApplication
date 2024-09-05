package com.example.controller;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

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
import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.domain.service.WeightService;
import com.example.form.TrainingGraphForm;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/training")
public class TrainingGraphController {
	@Autowired
	private ExerciseService exerciseService;
	
	@Autowired
	private WeightService weightService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}
		
	@GetMapping("/selectGraph")
	public String getSelectGraph(Model model, Authentication authentication) {
		setupModel(model,authentication);
		//筋トレグラフ選択画面を表示
		return "training/selectGraph";
	}
	
	//種目グラフ選択画面を表示
	@GetMapping("/exercise/graph/selectExerciseGraph")
	public String getExerciseGraph(@ModelAttribute TrainingGraphForm form, Model model, Authentication authentication) {
		setupModel(model,authentication);
		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList",bodyPartsList);
		
		//種目グラフ選択画面を表示
		return "training/exercise/graph/selectExerciseGraph";
	}
	
	//トレーニンググラフを表示する画面に遷移
	@PostMapping("/exercise/graph/selectExerciseGraph")
	public String postSelectExerciseGraph(@ModelAttribute @Validated TrainingGraphForm form,BindingResult bindingResult, Model model, Authentication authentication,HttpSession session) {
		setupModel(model,authentication);
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("trainingGraphForm",form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList",bodyPartsList);
			//部位は選択されていた場合
			if(form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId());
				model.addAttribute("exerciseList",exerciseList);
			}
			return "training/exercise/graph/selectExerciseGraph";
		}
		
		//セッションにフォームデータを保存
		session.setAttribute("trainingGraphForm",form);
		
		return "redirect:/training/exercise/graph/showTrainingGraph";
	}
	
	//トレーニンググラフを表示する画面を表示
	@GetMapping("/exercise/graph/showTrainingGraph")
	public String getShowTrainingGraph(Model model,Authentication authentication,HttpSession session,
				@RequestParam(value = "page", defaultValue = "1" )int page,
				@RequestParam(value = "size", defaultValue = "7")int size) {
		setupModel(model,authentication);
		
		TrainingGraphForm sessionForm = (TrainingGraphForm) session.getAttribute("trainingGraphForm");
		
		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());
		
		//特定の種目の、今までの最大重量を取得する
		Integer maxWeight = exerciseService.getMaxWeightByExerciseId(sessionForm.getExerciseId());
		
		//7日分のデータを取得
		int offset = (page - 1) * size;
		List<ExerciseRecord> maxWeightRecords = exerciseService.getMaxWeightForLast7Days(sessionForm.getExerciseId(), size, offset);
		int totalRecords = exerciseService.getMaxWeightRecords(sessionForm.getExerciseId());
		int totalPages = (int) Math.ceil((double)totalRecords / size);
		
		 // 日付と最大重量のリストを作成
	    List<String> dates = maxWeightRecords.stream()
	            .map(r -> new SimpleDateFormat("yyyy/MM/dd").format(r.getDate()))
	            .collect(Collectors.toList());
	    List<Integer> weights = maxWeightRecords.stream()
	            .map(ExerciseRecord::getWeight)
	            .collect(Collectors.toList());

		model.addAttribute("exerciseName",record.getExercise().getName());
			
		model.addAttribute("maxWeight",maxWeight);
		
		model.addAttribute("dates", dates);
		
		model.addAttribute("weights",weights);
		
		model.addAttribute("currentPage", page);
		
		model.addAttribute("totalPages",totalPages);
		
		return "training/exercise/graph/showTrainingGraph";
	}
	
	//体重グラフを表示する画面を表示
	@GetMapping("/weight/graph/showWeightGraph")
	public String getShowWeightGraph(Model model, Authentication authentication,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "7") int size) {
		setupModel(model,authentication);
		
		//今までの最低体重を取得
		int minBodyWeight = weightService.getMinBodyWeight();
		
		//今までの最大体重を取得
		int maxBodyWeight = weightService.getMaxBodyWeight();
		
		//7日分のデータを取得
		int offset = (page -1) * size;
		List<WeightRecord> bodyWeightRecords = weightService.getBodyWeightForLast7Days(size, offset);
		int totalRecords = weightService.getCountBodyWeightRecords();
		int totalPages = (int) Math.ceil((double)totalRecords / size);
		
		List<String> dates = bodyWeightRecords.stream()
	            .map(r -> new SimpleDateFormat("yyyy/MM/dd").format(r.getDate()))
	            .collect(Collectors.toList());
	    List<Integer> weights = bodyWeightRecords.stream()
	            .map(WeightRecord::getWeight)
	            .collect(Collectors.toList());
		
		model.addAttribute("minBodyWeight", minBodyWeight);
		
		model.addAttribute("maxBodyWeight", maxBodyWeight);
		
		model.addAttribute("dates", dates);
		
		model.addAttribute("weights",weights);
		
		model.addAttribute("currentPage", page);
		
		model.addAttribute("totalPages",totalPages);
		
		return "training/weight/graph/showWeightGraph";
	}
}
