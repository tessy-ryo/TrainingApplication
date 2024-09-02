package com.example.controller;

import java.util.List;

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

import com.example.domain.model.BodyParts;
import com.example.domain.model.Exercise;
import com.example.domain.model.ExerciseRecord;
import com.example.domain.service.CustomUserDetails;
import com.example.domain.service.ExerciseService;
import com.example.form.ExerciseDataForm;
import com.example.form.NoWeightExerciseDataForm;
import com.example.form.WeightExerciseDataForm;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/training")
public class TrainingEditController {
	@Autowired
	private ExerciseService exerciseService;
	
	//**認証されたユーザーのアカウントネームを表示するメソッド*/
			private void setupModel(Model model,Authentication authentication) {
				CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
				model.addAttribute("username",userDetails.getAccountName());
			}
			
			//トレーニング種目の編集画面を表示するメソッド
			@GetMapping("/edit/{id}")
			public String getEdit(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model, HttpSession session,@PathVariable("id") Integer id) {
				setupModel(model, authentication);
				
				session.removeAttribute("exerciseDataForm");
				
				//特定の筋トレデータ取得
				ExerciseRecord record = exerciseService.showSpecificData(id);
				
				//部位を取得
				List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
				model.addAttribute("bodyPartsList",bodyPartsList);
				
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(record.getBodyPartId());
				model.addAttribute("exerciseList",exerciseList);
				
				//id設定
				form.setId(record.getId());
				//日付け設定
				form.setDate(record.getDate());
				//部位設定
				form.setBodyPartId(record.getBodyPartId());
				//種目設定
				form.setExerciseId(record.getExerciseId());
				//重量設定
				form.setReps(record.getReps());
				//回数設定
				form.setWeight(record.getWeight());
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm",form);
				
				return "training/exercise/edit";
			}
			
			//トレーニング種目の編集画面を表示するメソッド
			@GetMapping("/edit")
			public String getEdit1(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model, HttpSession session) {
				setupModel(model, authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				//部位を取得
				List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
				model.addAttribute("bodyPartsList",bodyPartsList);
				
				form.setId(sessionForm.getId());
				//日付け設定
				form.setDate(sessionForm.getDate());
				//重量設定
				form.setReps(sessionForm.getReps());
				//回数設定
				form.setWeight(sessionForm.getWeight());
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm",form);
				return "training/exercise/edit";
			}
			
			@PostMapping("/edit")
			public String postEdit(@ModelAttribute @Validated ExerciseDataForm form, BindingResult bindingResult,HttpSession session,Authentication authentication,Model model) {
				//フォームデータを取り出してsessionFormに格納
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				if(bindingResult.hasErrors()) {
					model.addAttribute("exerciseDataForm",form);
					//部位を取得
					List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
					model.addAttribute("bodyPartsList",bodyPartsList);
					
					return "training/exercise/edit";
				}
				//日付け設定
				sessionForm.setDate(form.getDate());
				//部位設定
				sessionForm.setBodyPartId(form.getBodyPartId());
				//種目設定
				sessionForm.setExerciseId(form.getExerciseId());
				//もう一度セッションに変更点を保存
				session.setAttribute("exerciseDataForm", sessionForm);
				//重量確認
				int weightBased = exerciseService.checkWeightBased(sessionForm.getExerciseId());
				
				if(bindingResult.hasErrors()) {
					return getEdit(form, authentication, model, session, sessionForm.getId());
				}
				
				if(weightBased==0) {
					//重量がない場合
					return "redirect:/training/exercise/editReps";
				}else {
					//重量がある場合
					return "redirect:/training/exercise/editWeightReps";
				}
			}
			
			//回数を編集する画面を表示
			@GetMapping("/exercise/editReps")
			public String getEditReps(Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				//保存されたフォームの取り出し
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				model.addAttribute("noWeightExerciseDataForm",sessionForm);
				
				return "training/exercise/editReps";
			}
			
			@PostMapping("/exercise/editReps")
			public String postEditReps(@ModelAttribute @Validated NoWeightExerciseDataForm form,BindingResult bindingResult,HttpSession session,Authentication authentication,Model model) {
				setupModel(model, authentication);
				//保存されたフォームの取り出し
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				if(bindingResult.hasErrors()) {
					model.addAttribute("noWeightExerciseDataForm",form);
					
					return "training/exercise/editReps";
				}
				
				sessionForm.setReps(form.getReps());
				
				//筋トレデータを更新
				exerciseService.updateExerciseRecordOne(sessionForm.getDate(), sessionForm.getBodyPartId(), sessionForm.getExerciseId(), sessionForm.getWeight(), sessionForm.getReps(),sessionForm.getId());
				
				return "redirect:/training/exercise/editRepsSuccess";
				
			}
			
			//重量と回数を編集する画面を表示
			@GetMapping("/exercise/editWeightReps")
			public String getEditWeightReps(Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				model.addAttribute("weightExerciseDataForm",sessionForm);
				
				return "training/exercise/editWeightReps";
			}
			
			@PostMapping("/exercise/editWeightReps")
			public String postEditWeightReps(@ModelAttribute @Validated WeightExerciseDataForm form,BindingResult bindingResult, Model model,HttpSession session,Authentication authentication) {
				setupModel(model, authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				if(bindingResult.hasErrors()) {
					model.addAttribute("weightExerciseDataForm",form);
					
					return "training/exercise/editWeightReps";
				}
				//重量設定
				sessionForm.setReps(form.getReps());
				//回数設定
				sessionForm.setWeight(form.getWeight());
				
				//筋トレデータを更新
				exerciseService.updateExerciseRecordOne(sessionForm.getDate(), sessionForm.getBodyPartId(), sessionForm.getExerciseId(), sessionForm.getWeight(), sessionForm.getReps(),sessionForm.getId());
				
				return "redirect:/training/exercise/editWeightRepsSuccess";
				
			}
			
			@GetMapping("/exercise/editRepsSuccess")
			public String getEditRepsSuccess(Model model,HttpSession session, Authentication authentication) {
				setupModel(model,authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				ExerciseRecord record = exerciseService.showSpecificData(sessionForm.getId()); 
				
				sessionForm.setReps(record.getReps());
				
				sessionForm.setBodyPartName(record.getBodyParts().getName());
				
				sessionForm.setExerciseName(record.getExercise().getName());
				
				model.addAttribute("form",sessionForm);
				
				return "training/exercise/editRepsSuccess";
			}
			
			@GetMapping("/exercise/editWeightRepsSuccess")
			public String getEditWeightRepsSuccess(Model model,HttpSession session, Authentication authentication) {
				setupModel(model,authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				ExerciseRecord record = exerciseService.showSpecificData(sessionForm.getId()); 
				
				sessionForm.setWeight(record.getWeight());
				
				sessionForm.setReps(record.getReps());
				
				sessionForm.setBodyPartName(record.getBodyParts().getName());
				
				sessionForm.setExerciseName(record.getExercise().getName());
				
				model.addAttribute("form",sessionForm);
				
				return "training/exercise/editWeightRepsSuccess";
			}
			
			//トレーニングデータを削除する画面へ遷移
			@GetMapping("/delete/{id}")
			public String getDelete(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model,HttpSession session,@PathVariable("id") Integer id) {
				setupModel(model,authentication);
				
				//特定の筋トレデータ取得
				ExerciseRecord record = exerciseService.showSpecificData(id);
				
				int weightBased = exerciseService.checkWeightBased(record.getExerciseId());
				
				form.setDate(record.getDate());
				
				form.setBodyPartName(record.getBodyParts().getName());
				
				form.setExerciseName(record.getExercise().getName());
				
				form.setWeight(record.getWeight());
				
				form.setReps(record.getReps());
				
				form.setId(record.getId());
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm",form);
				
				if(weightBased==0) {
					//重量がない場合
					return "redirect:/training/exercise/deleteReps";
				}else {
					//重量がある場合
					return "redirect:/training/exercise/deleteWeightReps";
				}
			}
			
			//重量ありの削除画面を表示
			@GetMapping("/exercise/deleteWeightReps")
			public String deleteWeightReps(Authentication authentication,HttpSession session,Model model) {
				setupModel(model,authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				model.addAttribute("form",form);
				
				return "training/exercise/deleteWeightReps";
			}
			
			//重量なしの削除画面を表示
			@GetMapping("/exercise/deleteReps")
			public String deleteReps(Authentication authentication,HttpSession session,Model model) {
				setupModel(model,authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				model.addAttribute("form",form);
				
				return "training/exercise/deleteReps";
			}
			
			//重量あり筋トレデータ（１件）削除
			@PostMapping("/exercise/deleteWeightReps")
			public String postDeleteWeightReps(Authentication authentication,HttpSession session,Model model) {
				setupModel(model,authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				//筋トレデータ削除
				exerciseService.deleteExerciseRecordOne(form.getId());
				
				//ダッシュボード画面にリダイレクト
				return "redirect:/training/dashboard";
			}
			
			//重量なし筋トレデータ（１件）削除
			@PostMapping("/exercise/deleteReps")
			public String postDeleteReps(Authentication authentication,HttpSession session,Model model) {
				setupModel(model,authentication);
				
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				//筋トレデータ削除
				exerciseService.deleteExerciseRecordOne(form.getId());
				
				//ダッシュボード画面にリダイレクト
				return "redirect:/training/dashboard";
			}
			
			//種目を削除する画面を表示
			@GetMapping("/exercise/deleteExercise")
			public String deleteExercise(Authentication authentication,Model model,HttpSession session) {
				setupModel(model,authentication);
				
				//次画面のいいえボタンで戻る場合があるため保存されたフォームデータを取り出して値を表示
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				//次画面のいいえボタンで戻った際は種目を取得
				if(form != null) {
					List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId());
					model.addAttribute("exerciseList",exerciseList);
				}
						
				//フォームがセッションに存在しない場合新しいフォームを作成
				if(form == null) {
					form = new ExerciseDataForm();
				}
				
				model.addAttribute("form",form);
				
				//部位を取得
				List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
				model.addAttribute("bodyPartsList",bodyPartsList);
				//種目を選択する画面を表示
				return "training/exercise/deleteExercise";
			}
			
			//種目削除確認画面へ遷移
			@PostMapping("/exercise/deleteExercise")
			public String postDeleteExercise(@ModelAttribute ExerciseDataForm form, Authentication authentication,Model model,HttpSession session) {
				setupModel(model,authentication);
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm",form);
				
				return "redirect:/training/exercise/deleteExerciseCheck";
			}
			
			//種目削除確認画面を表示
			@GetMapping("/exercise/deleteExerciseCheck")
			public String deleteExerciseCheck(Authentication authentication,Model model,HttpSession session) {
				setupModel(model,authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				//筋トレ種目と種目ID、筋トレ部位を一件取得
				ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());
				
				//sessionFormに種目IDを設定
				sessionForm.setExerciseId(record.getExerciseId());
				
				//sessionFormに筋トレ部位名を設定
				sessionForm.setBodyPartName(record.getBodyParts().getName());
				
				//sessionFormに筋トレ種目名を設定
				sessionForm.setExerciseName(record.getExercise().getName());
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm",sessionForm);
				
				// モデルにフォームデータを追加
			    model.addAttribute("exerciseDataForm", sessionForm);
				
				return "training/exercise/deleteExerciseCheck";
			}
			
			//種目が削除され、種目を選択する画面に遷移する
			@PostMapping("/exercise/deleteExerciseCheck")
			public String postDeleteExerciseCheck(Authentication authentication,Model model,HttpSession session) {
				setupModel(model,authentication);
				
				//保存されたフォームの取り出し
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				//筋トレ種目を種目IDで論理削除する
				exerciseService.softDeleteExercise(sessionForm.getExerciseId());
				
				return "redirect:/training/dashboard";
			}
			
			//種目を追加する画面を表示
			@GetMapping("/exercise/addExercise")
			public String addExercise(Authentication authentication,Model model,HttpSession session) {
				setupModel(model,authentication);
				
				//次画面のいいえボタンで戻る場合があるため保存されたフォームデータを取り出して値を表示
				ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm") ;
				
				//次画面のいいえボタンで戻った際は種目を取得
				if(form != null) {
					List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId());
					model.addAttribute("exerciseList",exerciseList);
				}
						
				//フォームがセッションに存在しない場合新しいフォームを作成
				if(form == null) {
					form = new ExerciseDataForm();
				}
				
				model.addAttribute("form",form);
				
				//部位を取得
				List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
				model.addAttribute("bodyPartsList",bodyPartsList);
				//種目を選択する画面を表示
				return "training/exercise/addExercise";
			}
			
			//種目追加確認画面へ遷移
			@PostMapping("/exercise/addExercise")
			public String postAddExercise(@ModelAttribute ExerciseDataForm form,Authentication authentication,Model model,HttpSession session) {
				setupModel(model,authentication);
				
				if(form.getWeightBased() == 1) {
					form.setWeightBasedText("あり");
				}else if(form.getWeightBased() == 0) {
					form.setWeightBasedText("なし");
				}
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm", form);
				
				return "redirect:/training/exercise/addExerciseCheck";
			}
			
			//種目追加確認画面を表示
			@GetMapping("/exercise/addExerciseCheck")
			public String addExerciseCheck(Authentication authentication,Model model,HttpSession session) {
				setupModel(model,authentication);
				
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				BodyParts bodyPart = exerciseService.getOneBodyPart(sessionForm.getBodyPartId());
				
				sessionForm.setBodyPartName(bodyPart.getName());
				
				//セッションにフォームデータを保存
				session.setAttribute("exerciseDataForm", sessionForm);
				
				model.addAttribute("exerciseDataForm",sessionForm);
				
				return "training/exercise/addExerciseCheck";
			}
			
			//種目が追加され、種目選択画面に移動
			@PostMapping("/exercise/addExerciseCheck")
			public String postExerciseCheck(Authentication authentication,Model model,HttpSession session) {
				ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
				
				exerciseService.addExercise(sessionForm.getExerciseName(),sessionForm.getBodyPartId(),sessionForm.getWeightBased());
				
				//セッションフォームのデータを破棄
				session.removeAttribute("exerciseDataForm");
				
				return "redirect:/training/exercise/selectExercise";
			}
}
