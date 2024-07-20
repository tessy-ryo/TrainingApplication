package com.example.application.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class ExerciseApplicationService {

	/**部位のMapを生成する*/
	public Map<String,Integer> getBodyPartsMap(){
		Map<String,Integer> bodyPartsMap = new LinkedHashMap<>();
		bodyPartsMap.put("胸",1);
		bodyPartsMap.put("背中",2);
		bodyPartsMap.put("肩",3);
		bodyPartsMap.put("脚",4);
		bodyPartsMap.put("上腕二頭筋",5);
		bodyPartsMap.put("上腕三頭筋",6);
		bodyPartsMap.put("腹筋",7);
		
		return bodyPartsMap;
	}
}
