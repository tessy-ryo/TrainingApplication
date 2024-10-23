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
@RequestMapping("/training")
public class BodyWeightRecordController {
	@Autowired
	private WeightService weightService;
	
	@Autowired
	private ModelMapper modelMapper;
		
		@GetMapping("/weight/record/weightHistory")
		public String getWeightHistory(@ModelAttribute HistoryForm form,
				@RequestParam(value="page",defaultValue="1") int page,
				@RequestParam(value="size",defaultValue="6") int size,
				Model model,HttpSession session,Authentication authentication,HttpServletRequest request) {
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
		
		//**体重を記録する画面を表示*/
		@GetMapping("/record/weight")
		public String getRecordWeight(@ModelAttribute BodyWeightDataForm form,Model model,Authentication authentication,HttpSession session) {
			
			
			//現在の日付をフォームのdateフィールドにセット
			form.setDate(LocalDate.now());
			
			//体重を記録する画面を表示
			return "training/weight/record/recordWeight";
		}
		
		//**体重記録確認画面に移動*/
		@PostMapping("/record/weight")
		public String postRecordWeight(@Valid @ModelAttribute BodyWeightDataForm form,BindingResult bindingResult, Model model,HttpSession session, Authentication authentication) {
			
			
			if(bindingResult.hasErrors()) {
				model.addAttribute("recordBodyWeightForm",form);
				
				return "training/weight/record/recordWeight";
			}
			//セッションにフォームデータを保存
			session.setAttribute("recordBodyWeightForm", form);
			
			return "redirect:/training/weight/checkWeightRecord";
		}
		
		//**体重記録確認画面を表示*/
		@GetMapping("/weight/checkWeightRecord")
			public String checkWeightRecord(Model model, HttpSession session, Authentication authentication) {
			
			//セッションからフォームデータを取得
			BodyWeightDataForm form = (BodyWeightDataForm) session.getAttribute("recordBodyWeightForm");
			
			model.addAttribute("recordBodyWeightForm", form);
			
			return "training/weight/record/checkWeightRecord";
		}
		
		@PostMapping("/weight/checkWeightRecord")
			public String confirmWeightRecord(Model model, HttpSession session, Authentication authentication) {
			
			
			//セッションからフォームデータを取得
			BodyWeightDataForm form = (BodyWeightDataForm) session.getAttribute("recordBodyWeightForm");
			
			WeightRecord record = modelMapper.map(form,WeightRecord.class);
			//体重を記録
			weightService.recordWeight(record,authentication);
			
			//セッションのフォームデータを破棄
			session.removeAttribute("recordBodyWeightForm");
			
			return "redirect:/training/weight/record/weightHistory";
		}
			
		
}
