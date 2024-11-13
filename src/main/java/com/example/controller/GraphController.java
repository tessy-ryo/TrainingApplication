package com.example.controller;

import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.PathVariable;
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

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/graph")
public class GraphController {
	@Autowired
	private ExerciseService exerciseService;
	
	@Autowired
	private WeightService weightService;

		
	@GetMapping("")
	public String getSelectGraph(Model model, Authentication authentication,HttpServletRequest request) {
		model.addAttribute("currentUri",request.getRequestURI());
		//筋トレグラフ選択画面を表示
		return "training/selectGraph";
	}
	
	//種目グラフ選択画面を表示
	@GetMapping("/exercise")
	public String getExerciseGraph(@ModelAttribute TrainingGraphForm form, Model model) {
		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList",bodyPartsList);
		
		//種目グラフ選択画面を表示
		return "training/exercise/graph/selectExerciseGraph";
	}
	
	//トレーニンググラフを表示する画面に遷移
	@PostMapping("/exercise")
	public String postSelectExerciseGraph(@ModelAttribute @Validated TrainingGraphForm form,BindingResult bindingResult, Model model, Authentication authentication) {
		
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		
		if(bindingResult.hasErrors()) {
			model.addAttribute("trainingGraphForm",form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList",bodyPartsList);
			//部位は選択されていた場合
			if(form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId(),userDetails.getId());
				model.addAttribute("exerciseList",exerciseList);
			}
			return "training/exercise/graph/selectExerciseGraph";
		}
		
		return "redirect:/graph/exercise/"+form.getExerciseId();
	}
	
	//トレーニンググラフを表示する画面を表示
	@GetMapping("/exercise/{exerciseId}")
	public String getShowTrainingGraph(@PathVariable("exerciseId") Integer exerciseId, Model model, Authentication authentication,
	        @RequestParam(value = "page", required = false) Integer page,  
	        @RequestParam(value = "size", defaultValue = "7") int size) {
	    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

	    // 筋トレ種目と種目ID、筋トレ部位を一件取得
	    ExerciseRecord record = exerciseService.getOneExercise(exerciseId);

	    // 特定の種目の、今までの最大重量を取得する
	    Double maxWeight = exerciseService.getMaxWeightByExerciseId(exerciseId, userDetails.getId());

	    // 総レコード数を取得
	    int totalRecords = exerciseService.getMaxWeightRecords(exerciseId, userDetails.getId());
	    // 合計ページ数を計算
	    int totalPages = totalRecords == 0 ? 1 : (int) Math.ceil((double) totalRecords / size);

	    // pageがnullの場合のみ totalPages にリダイレクト
	    if (page == null) {
	        return "redirect:/graph/exercise/"+ exerciseId + "?page=" + totalPages + "&size=" + size;
	    }

	    // 7日分のデータを取得
	    int offset = (page - 1) * size;
	    List<ExerciseRecord> maxWeightRecords = exerciseService.getMaxWeightForLast7Days(exerciseId, userDetails.getId(), size, offset);

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	    
	    // 日付と最大重量のリストを作成
	    List<String> dates = maxWeightRecords.stream()
	            .map(r -> r.getDate().format(formatter))
	            .collect(Collectors.toList());
	    List<Double> weights = maxWeightRecords.stream()
	            .map(ExerciseRecord::getWeight)
	            .collect(Collectors.toList());

	    model.addAttribute("exerciseName", record.getExercise().getName());
	    model.addAttribute("maxWeight", maxWeight);
	    model.addAttribute("dates", dates);
	    model.addAttribute("weights", weights);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("exerciseId", exerciseId);
	    model.addAttribute("size", size);


	    return "training/exercise/graph/showTrainingGraph";
	}

	
	//体重グラフを表示する画面を表示
	@GetMapping("/weight")
	public String getShowWeightGraph(Model model, Authentication authentication,
	        @RequestParam(value = "page", required = false) Integer page, 
	        @RequestParam(value = "size", defaultValue = "7") int size) {
	    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

	    // 今までの最低体重と最大体重を取得
	    Double minBodyWeight = weightService.getMinBodyWeight(userDetails.getId());
	    Double maxBodyWeight = weightService.getMaxBodyWeight(userDetails.getId());

	    // 総レコード数を取得
	    int totalRecords = weightService.getCountBodyWeightRecords(userDetails.getId());
	    // 合計ページ数を計算
	    int totalPages = totalRecords == 0 ? 1 : (int) Math.ceil((double) totalRecords / size);
	    
	    // pageがnullの場合のみ totalPages にリダイレクト
	    if (page == null) {
	        return "redirect:/graph/weight?page=" + totalPages + "&size=" + size;
	    }

	    // 7日分のデータを取得
	    int offset = (page - 1) * size;
	    List<WeightRecord> bodyWeightRecords = weightService.getBodyWeightForLast7Days(userDetails.getId(), size, offset);

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
	    List<String> dates = bodyWeightRecords.stream()
	            .map(r -> r.getDate().format(formatter))
	            .collect(Collectors.toList());
	    List<Double> weights = bodyWeightRecords.stream()
	            .map(WeightRecord::getBodyWeight)
	            .collect(Collectors.toList());

	    model.addAttribute("minBodyWeight", minBodyWeight);
	    model.addAttribute("maxBodyWeight", maxBodyWeight);
	    model.addAttribute("dates", dates);
	    model.addAttribute("weights", weights);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);
	    model.addAttribute("size",size);

	    return "training/weight/graph/showWeightGraph";
	}

}
