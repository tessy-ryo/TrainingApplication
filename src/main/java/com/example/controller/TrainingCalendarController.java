package com.example.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.domain.model.ExerciseRecord;
import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.domain.service.WeightService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/training")
public class TrainingCalendarController {
	@Autowired
	private ExerciseService exerciseService;
	
	@Autowired
	private WeightService weightService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication,HttpServletRequest request) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
		model.addAttribute("currentUri",request.getRequestURI());
	}
	
	@GetMapping("/exercise/calendar/showTrainingCalendar")
	public String getTrainingCalendar(Model model, Authentication authentication,HttpServletRequest request) throws JsonProcessingException {
		setupModel(model, authentication, request);
		
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		
		//重複しないトレーニング日を取得
		List<ExerciseRecord> records = exerciseService.getAllDistinctTrainingDate(userDetails.getId());
		
		List<Map<String, Object>> events = new ArrayList<>();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		
		for (ExerciseRecord record : records) {
			Map<String, Object> event = new HashMap<>();
			event.put("title","筋トレ");
			event.put("start", record.getDate().format(formatter));
			event.put("color", "red");
			events.add(event);
		}
		
		List<WeightRecord> weightRecords = weightService.getAllDistinctWeightRecordsDate(userDetails.getId());
		
		for(WeightRecord record : weightRecords) {
			Map<String, Object> event = new HashMap<>();
	        event.put("title", "体重記録");
	        event.put("start", record.getDate().format(formatter));
	        event.put("color", "blue");
	        events.add(event);
		}

		String eventsJson = new ObjectMapper().writeValueAsString(events);
		model.addAttribute("eventsJson",eventsJson);
		
		return "training/exercise/calendar/showTrainingCalendar";
	}
}
