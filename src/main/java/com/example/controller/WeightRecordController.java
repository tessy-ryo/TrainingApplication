package com.example.controller;

import java.time.LocalDate;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.domain.model.WeightRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.WeightService;
import com.example.form.BodyWeightDataForm;
import com.example.form.HistoryForm;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/weight")
public class WeightRecordController {
	@Autowired
	private WeightService weightService;
	
	@Autowired 
	private ModelMapper modelMapper;
		
		@GetMapping("")
		public String getWeightHistory(@ModelAttribute HistoryForm form,
				@RequestParam(value="page",defaultValue="1") int page,
				@RequestParam(value="size",defaultValue="6") int size,
				Model model,HttpSession session,Authentication authentication,HttpServletRequest request) {
			//入力画面の途中で画面を遷移し、セッションにデータが残っている場合に、データを破棄
			if(session != null) {
				session.removeAttribute("exerciseDataForm");
				session.removeAttribute("bodyWeightDataForm");
				session.removeAttribute("deleteExerciseForm");
				session.removeAttribute("addExerciseForm");
			}
			//ダッシュボード画面を表示
			model.addAttribute("currentUri",request.getRequestURI());
			
			CustomUserDetails userDetails = (CustomUserDetails)authentication.getPrincipal();
			form.setUserId(userDetails.getId());

			int offset = (page - 1) * size;
			
			List<WeightRecord> weightList = weightService.getBodyWeight(form.getUserId(),form.getSearchName(),size, offset);
			
			//ユーザーの体重データのレコード数を検索を含めてカウントする
			Integer totalRecords = weightService.getCountBodyWeightData(form.getUserId(),form.getSearchName());
			
			int totalPages = 0;
			
			if (totalRecords == 0) {
				totalPages = 1;
			}else {
				//レコード数をsizeで割って、合計ページを計算する
				totalPages = (int)Math.ceil((double)totalRecords / size);
			}
				
			model.addAttribute("weightList",weightList);
			
			model.addAttribute("currentPage",page);
			
			model.addAttribute("totalPages", totalPages);
			
			return "training/weight/record/weightHistory";
		}
		
		//体重を記録する画面を表示
		@GetMapping("/add/bodyWeight")
		public String getRecordWeight(Model model,Authentication authentication,HttpSession session) {
			
			BodyWeightDataForm form = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");
			
			if(form==null) {
				form = new BodyWeightDataForm();
			}
			
			//現在の日付をフォームのdateフィールドにセット
			form.setDate(LocalDate.now());
			
			model.addAttribute("bodyWeightDataForm",form);
			//体重を記録する画面を表示
			return "training/weight/record/recordWeight";
		}
		
		//体重記録確認画面に移動
		@PostMapping("/add/bodyWeight")
		public String postRecordWeight(@Valid @ModelAttribute BodyWeightDataForm form,BindingResult bindingResult, Model model,HttpSession session, Authentication authentication) {
			if(bindingResult.hasErrors()) {
				return "training/weight/record/recordWeight";
			}
			//セッションにフォームデータを保存
			session.setAttribute("bodyWeightDataForm", form);
			
			return "redirect:/weight/check/bodyWeight";
		}
		
		//体重記録確認画面を表示
		@GetMapping("/check/bodyWeight")
			public String checkWeightRecord(Model model,HttpSession session) throws Exception{
			
			//セッションからフォームデータを取得
			BodyWeightDataForm form = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");
			
			if (form == null) {
	            throw new Exception("不正な操作が行われました");
	        }
			
			model.addAttribute("bodyWeightDataForm", form);
			
			return "training/weight/record/checkWeightRecord";
		}
		
		@PostMapping("/check/bodyWeight")
			public String confirmWeightRecord(Model model, HttpSession session, Authentication authentication) {
	
			//セッションからフォームデータを取得
			BodyWeightDataForm form = (BodyWeightDataForm) session.getAttribute("bodyWeightDataForm");
			
			WeightRecord record = modelMapper.map(form,WeightRecord.class);
			//体重を記録
			weightService.recordWeight(record,authentication);
			
			//セッションのフォームデータを破棄
			session.removeAttribute("bodyWeightDataForm");
			
			return "redirect:/weight";
		}
}
