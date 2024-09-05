package com.example.controller;

import java.text.SimpleDateFormat;
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
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/training")
public class TrainingCalendarController {
	@Autowired
	private ExerciseService exerciseService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
	private void setupModel(Model model,Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username",userDetails.getAccountName());
	}
	
	@GetMapping("/exercise/calendar/showTrainingCalendar")
	public String getTrainingCalendar(Model model, Authentication authentication) throws JsonProcessingException {
		setupModel(model, authentication);
		
		CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
		
		//重複しないトレーニング日を取得
		List<ExerciseRecord> records = exerciseService.getAllTrainingDate(userDetails.getId());
		
		List<Map<String, Object>> events = new ArrayList<>();
		for (ExerciseRecord record : records) {
			Map<String, Object> event = new HashMap<>();
			event.put("title","筋トレ");
			event.put("start", new SimpleDateFormat("yyyy-MM-dd").format(record.getDate()));
			event.put("color", "red");
			events.add(event);
		}

		String eventsJson = new ObjectMapper().writeValueAsString(events);
		model.addAttribute("eventsJson",eventsJson);
		
		return "training/exercise/calendar/showTrainingCalendar";
	}
}