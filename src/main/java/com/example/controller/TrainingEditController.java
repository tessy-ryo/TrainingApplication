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
import com.example.form.AddExerciseForm;
import com.example.form.DeleteExerciseForm;
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
	private void setupModel(Model model, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		model.addAttribute("username", userDetails.getAccountName());
	}

	//トレーニング種目の編集画面を表示するメソッド
	@GetMapping("/edit/{id}")
	public String getEdit(@ModelAttribute ExerciseDataForm form, Authentication authentication, Model model,
			HttpSession session, @PathVariable("id") Integer id) {
		setupModel(model, authentication);

		//特定の筋トレデータ取得
		ExerciseRecord record = exerciseService.showSpecificData(id);

		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList", bodyPartsList);

		//種目を取得
		List<Exercise> exerciseList = exerciseService.getExercises(record.getBodyPartId());
		model.addAttribute("exerciseList", exerciseList);

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
		session.setAttribute("exerciseDataForm", form);

		return "training/exercise/edit/edit";
	}

	@PostMapping("/edit")
	public String postEdit(@ModelAttribute @Validated ExerciseDataForm form, BindingResult bindingResult,
			HttpSession session, Authentication authentication, Model model) {
		//フォームデータを取り出してsessionFormに格納
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");

		if (bindingResult.hasErrors()) {
			model.addAttribute("exerciseDataForm", form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList", bodyPartsList);
			//部位は選択されていた場合
			if (form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId());
				model.addAttribute("exerciseList", exerciseList);
			}

			return "training/exercise/edit/edit";
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

		if (weightBased == 0) {
			//重量がない場合
			return "redirect:/training/exercise/editReps";
		} else {
			//重量がある場合
			return "redirect:/training/exercise/editWeightReps";
		}
	}

	//回数を編集する画面を表示
	@GetMapping("/exercise/editReps")
	public String getEditReps(Model model, HttpSession session, Authentication authentication) {
		setupModel(model, authentication);
		//保存されたフォームの取り出し
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		model.addAttribute("noWeightExerciseDataForm", sessionForm);

		return "training/exercise/edit/editReps";
	}

	@PostMapping("/exercise/editReps")
	public String postEditReps(@ModelAttribute @Validated NoWeightExerciseDataForm form, BindingResult bindingResult,
			HttpSession session, Authentication authentication, Model model) {
		setupModel(model, authentication);
		//保存されたフォームの取り出し
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");

		if (bindingResult.hasErrors()) {
			model.addAttribute("noWeightExerciseDataForm", form);

			return "training/exercise/edit/editReps";
		}

		//重量設定
		sessionForm.setWeight(null);
		//回数設定
		sessionForm.setReps(form.getReps());
		
		//もう一度、セッションにフォームデータを保存
		session.setAttribute("exerciseDataForm", sessionForm);

		return "redirect:/training/exercise/editRepsCheck";

	}
	
	@GetMapping("/exercise/editRepsCheck")
	public String getEditRepsCheck(Authentication authentication, Model model, HttpSession session) {
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
				
		return "training/exercise/edit/editRepsCheck";
	}
	
	@PostMapping("/exercise/editRepsCheck")
	public String postEditRepsCheck(Model model, Authentication authentication, HttpSession session) {
		setupModel(model,authentication);
		
		//保存されたフォームの取り出し
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		//筋トレデータを更新
		exerciseService.updateExerciseRecordOne(sessionForm.getDate(), sessionForm.getBodyPartId(),
				sessionForm.getExerciseId(), sessionForm.getWeight(), sessionForm.getReps(), sessionForm.getId());
		
		session.removeAttribute("exerciseDataForm");
		
		return "redirect:/training/exercise/record/trainingHistory";
	}

	//重量と回数を編集する画面を表示
	@GetMapping("/exercise/editWeightReps")
	public String getEditWeightReps(Model model, HttpSession session, Authentication authentication) {
		setupModel(model, authentication);

		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		model.addAttribute("weightExerciseDataForm", sessionForm);

		return "training/exercise/edit/editWeightReps";
	}

	@PostMapping("/exercise/editWeightReps")
	public String postEditWeightReps(@ModelAttribute @Validated WeightExerciseDataForm form,
			BindingResult bindingResult, Model model, HttpSession session, Authentication authentication) {
		setupModel(model, authentication);

		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");

		if (bindingResult.hasErrors()) {
			model.addAttribute("weightExerciseDataForm", form);

			return "training/exercise/edit/editWeightReps";
		}
		//回数設定
		sessionForm.setReps(form.getReps());
		//重量設定
		sessionForm.setWeight(form.getWeight());
		
		//もう一度セッションにフォームデータを保存
		session.setAttribute("exerciseDataForm", sessionForm);
		
		return "redirect:/training/exercise/editWeightRepsCheck";

	}
	
	@GetMapping("/exercise/editWeightRepsCheck")
	public String getEditWeightRepsCheck(Model model, HttpSession session, Authentication authentication) {
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
						
		return "training/exercise/edit/editWeightRepsCheck";
	}
	
	@PostMapping("/exercise/editWeightRepsCheck")
	public String postEditWeightRepsCheck(Model model, Authentication authentication, HttpSession session) {
		setupModel(model,authentication);
		
		//保存されたフォームの取り出し
		ExerciseDataForm sessionForm = (ExerciseDataForm) session.getAttribute("exerciseDataForm");
		
		//筋トレデータを更新
		exerciseService.updateExerciseRecordOne(sessionForm.getDate(), sessionForm.getBodyPartId(),
				sessionForm.getExerciseId(), sessionForm.getWeight(), sessionForm.getReps(), sessionForm.getId());
		
		session.removeAttribute("exerciseDataForm");
		
		return "redirect:/training/exercise/record/trainingHistory";
	}

	//トレーニングデータを削除する画面へ遷移
	@GetMapping("/delete/{id}")
	public String getDelete(@ModelAttribute ExerciseDataForm form, Authentication authentication, Model model,
			HttpSession session, @PathVariable("id") Integer id) {
		setupModel(model, authentication);

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
		session.setAttribute("exerciseDataForm", form);

		if (weightBased == 0) {
			//重量がない場合
			return "training/exercise/edit/deleteReps";
		} else {
			//重量がある場合
			return "training/exercise/edit/deleteWeightReps";
		}
	}

	//重量あり筋トレデータ（１件）削除
	@PostMapping("/exercise/deleteWeightReps")
	public String postDeleteWeightReps(Authentication authentication, HttpSession session, Model model) {
		setupModel(model, authentication);

		ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm");

		//筋トレデータ削除
		exerciseService.deleteExerciseRecordOne(form.getId());
		
		session.removeAttribute("exerciseDataForm");

		//筋トレ記録画面にリダイレクト
		return "redirect:/training/exercise/record/trainingHistory";
	}

	//重量なし筋トレデータ（１件）削除
	@PostMapping("/exercise/deleteReps")
	public String postDeleteReps(Authentication authentication, HttpSession session, Model model) {
		setupModel(model, authentication);

		ExerciseDataForm form = (ExerciseDataForm) session.getAttribute("exerciseDataForm");

		//筋トレデータ削除
		exerciseService.deleteExerciseRecordOne(form.getId());
		
		session.removeAttribute("exerciseDataForm");

		//筋トレ記録画面にリダイレクト
		return "redirect:/training/exercise/record/trainingHistory";
	}

	//種目を削除する画面を表示
	@GetMapping("/exercise/deleteExercise")
	public String deleteExercise(@ModelAttribute DeleteExerciseForm form,Authentication authentication, Model model, HttpSession session) {
		setupModel(model, authentication);
		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList", bodyPartsList);
		
		//種目を選択する画面を表示
		return "training/exercise/edit/deleteExercise";
	}

	//種目削除確認画面へ遷移
	@PostMapping("/exercise/deleteExercise")
	public String postDeleteExercise(@ModelAttribute @Validated DeleteExerciseForm form,BindingResult bindingResult, Authentication authentication, Model model,
			HttpSession session) {
		setupModel(model, authentication);
		if (bindingResult.hasErrors()) {
			model.addAttribute("deleteExerciseForm", form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList", bodyPartsList);
			//部位は選択されていた場合
			if (form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId());
				model.addAttribute("exerciseList", exerciseList);
			}

			return "training/exercise/edit/deleteExercise";
		}

		//セッションにフォームデータを保存
		session.setAttribute("deleteExerciseForm", form);

		return "redirect:/training/exercise/deleteExerciseCheck";
	}

	//種目削除確認画面を表示
	@GetMapping("/exercise/deleteExerciseCheck")
	public String deleteExerciseCheck(Authentication authentication, Model model, HttpSession session) {
		setupModel(model, authentication);

		DeleteExerciseForm sessionForm = (DeleteExerciseForm) session.getAttribute("deleteExerciseForm");

		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(sessionForm.getExerciseId());

		//sessionFormに種目IDを設定
		sessionForm.setExerciseId(record.getExerciseId());

		//sessionFormに筋トレ部位名を設定
		sessionForm.setBodyPartName(record.getBodyParts().getName());

		//sessionFormに筋トレ種目名を設定
		sessionForm.setExerciseName(record.getExercise().getName());

		//セッションにフォームデータを保存
		session.setAttribute("deleteExerciseForm", sessionForm);

		// モデルにフォームデータを追加
		model.addAttribute("deleteExerciseForm", sessionForm);

		return "training/exercise/edit/deleteExerciseCheck";
	}

	//種目が削除され、ダッシュボード画面に遷移する
	@PostMapping("/exercise/deleteExerciseCheck")
	public String postDeleteExerciseCheck(Authentication authentication, Model model, HttpSession session) {
		setupModel(model, authentication);

		//保存されたフォームの取り出し
		DeleteExerciseForm sessionForm = (DeleteExerciseForm) session.getAttribute("deleteExerciseForm");

		//筋トレ種目を種目IDで論理削除する
		exerciseService.softDeleteExercise(sessionForm.getExerciseId());
		
		//セッションフォームのデータを破棄
		session.removeAttribute("deleteExerciseForm");

		return "redirect:/training/exercise/record/trainingHistory";
	}

	//種目を追加する画面を表示
	@GetMapping("/exercise/addExercise")
	public String addExercise(@ModelAttribute AddExerciseForm form, Authentication authentication, Model model,
			HttpSession session) {
		setupModel(model, authentication);

		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList", bodyPartsList);
		//種目を選択する画面を表示
		return "training/exercise/edit/addExercise";
	}

	//種目追加確認画面へ遷移
	@PostMapping("/exercise/addExercise")
	public String postAddExercise(@ModelAttribute @Validated AddExerciseForm form, BindingResult bindingResult,
			Authentication authentication, Model model, HttpSession session) {
		setupModel(model, authentication);

		if (bindingResult.hasErrors()) {
			return addExercise(form, authentication, model, session);
		}

		if (form.getWeightBased() == 1) {
			form.setWeightBasedText("あり");
		} else if (form.getWeightBased() == 0) {
			form.setWeightBasedText("なし");
		}

		//セッションにフォームデータを保存
		session.setAttribute("addExerciseForm", form);

		return "redirect:/training/exercise/addExerciseCheck";
	}

	//種目追加確認画面を表示
	@GetMapping("/exercise/addExerciseCheck")
	public String addExerciseCheck(Authentication authentication, Model model, HttpSession session) {
		setupModel(model, authentication);

		AddExerciseForm sessionForm = (AddExerciseForm) session.getAttribute("addExerciseForm");

		BodyParts bodyPart = exerciseService.getOneBodyPart(sessionForm.getBodyPartId());

		sessionForm.setBodyPartName(bodyPart.getName());

		//セッションにフォームデータを保存
		session.setAttribute("addExerciseForm", sessionForm);

		model.addAttribute("addExerciseForm", sessionForm);

		return "training/exercise/edit/addExerciseCheck";
	}

	//種目が追加され、種目選択画面に移動
	@PostMapping("/exercise/addExerciseCheck")
	public String postExerciseCheck(Authentication authentication, Model model, HttpSession session) {
		AddExerciseForm sessionForm = (AddExerciseForm) session.getAttribute("addExerciseForm");

		exerciseService.addExercise(sessionForm.getExerciseName(), sessionForm.getBodyPartId(),
				sessionForm.getWeightBased());

		//セッションフォームのデータを破棄
		session.removeAttribute("addExerciseForm");

		return "redirect:/training/exercise/selectExercise";
	}
}
