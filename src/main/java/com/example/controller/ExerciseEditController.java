package com.example.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
@RequestMapping("/exercise")
public class ExerciseEditController {
	@Autowired
	private ExerciseService exerciseService;

	@ModelAttribute
	public void checkExerciseAccess(@ModelAttribute ExerciseDataForm form, Authentication authentication) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		if (form.getExerciseId() != null) {
			ExerciseRecord record = exerciseService.getUserIdByExerciseId(form.getExerciseId());

			// recordがnullでない場合のみアクセス権限をチェック
			if (record != null && !record.getUserId().equals(userDetails.getId())) {
				throw new AccessDeniedException("不正なアクセスです");
			}
		}
		
		if(form.getId() != null) {
			//特定の筋トレデータ取得
			ExerciseRecord record = exerciseService.showSpecificData(form.getId());

			// 現在のユーザーがこのデータにアクセスできるか確認
			if (record.getUserId() != (userDetails.getId())) {
				throw new AccessDeniedException("不正なアクセスです");
			}
		}
	}

	//トレーニング種目の編集画面を表示するメソッド
	@GetMapping("/edit/{id}/event")
	public String getEdit(@ModelAttribute ExerciseDataForm form, Authentication authentication, Model model) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//特定の筋トレデータ取得
		ExerciseRecord record = exerciseService.showSpecificData(form.getId());

		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList", bodyPartsList);

		//種目を取得
		List<Exercise> exerciseList = exerciseService.getExercises(record.getBodyPartId(), userDetails.getId());
		model.addAttribute("exerciseList", exerciseList);

		//日付け設定
		form.setDate(record.getDate());
		//部位設定
		form.setBodyPartId(record.getBodyPartId());
		//種目設定
		form.setExerciseId(record.getExerciseId());

		return "training/exercise/edit/edit";
	}

	@PostMapping("/edit/{id}/event")
	public String postEdit(@ModelAttribute @Validated ExerciseDataForm form, BindingResult bindingResult,
			Authentication authentication, Model model, RedirectAttributes redirectAttributes) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		if (bindingResult.hasErrors()) {
			model.addAttribute("exerciseDataForm", form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList", bodyPartsList);
			//部位は選択されていた場合
			if (form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId(), userDetails.getId());
				model.addAttribute("exerciseList", exerciseList);
			}

			return "training/exercise/edit/edit";
		}
		//重量確認
		int weightBased = exerciseService.checkWeightBased(form.getExerciseId());
		
		redirectAttributes.addAttribute("id", form.getId());
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());

		if (weightBased == 0) {
			//重量がない場合
			return "redirect:/exercise/edit/{id}/reps";
		} else {
			//重量がある場合
			return "redirect:/exercise/edit/{id}/weight";
		}
	}

	//回数を編集する画面を表示
	@GetMapping("/edit/{id}/reps")
	public String getEditReps(@ModelAttribute NoWeightExerciseDataForm form) {
		//特定の筋トレデータ取得
		ExerciseRecord record = exerciseService.showSpecificData(form.getId());
				
		form.setReps(record.getReps());
				
		form.setWeight(record.getWeight());
				
		return "training/exercise/edit/editReps";
	}

	@PostMapping("/edit/{id}/reps")
	public String postEditReps(@ModelAttribute @Validated NoWeightExerciseDataForm form, BindingResult bindingResult,
			Model model, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("noWeightExerciseDataForm", form);

			return "training/exercise/edit/editReps";
		}

		redirectAttributes.addAttribute("id", form.getId());
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
		redirectAttributes.addAttribute("reps", form.getReps());

		return "redirect:/exercise/edit/{id}/repsCheck";

	}

	@GetMapping("/edit/{id}/repsCheck")
	public String getEditRepsCheck(@ModelAttribute ExerciseDataForm form) {

		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(form.getExerciseId());

		//筋トレ部位を設定
		form.setBodyPartName(record.getBodyParts().getName());
		//筋トレ種目を設定
		form.setExerciseName(record.getExercise().getName());

		return "training/exercise/edit/editRepsCheck";
	}

	@PostMapping("/edit/{id}/repsCheck")
	public String postEditRepsCheck(@ModelAttribute ExerciseDataForm form) {

		//筋トレデータを更新
		exerciseService.updateExerciseRecordOne(form.getDate(), form.getBodyPartId(),
				form.getExerciseId(), form.getWeight(), form.getReps(), form.getId());

		return "redirect:/exercise";
	}

	//重量と回数を編集する画面を表示
	@GetMapping("/edit/{id}/weight")
	public String getEditWeightReps(@ModelAttribute WeightExerciseDataForm form) {
		//特定の筋トレデータ取得
		ExerciseRecord record = exerciseService.showSpecificData(form.getId());
		
		form.setReps(record.getReps());
		
		form.setWeight(record.getWeight());
		
		return "training/exercise/edit/editWeightReps";
	}

	@PostMapping("/edit/{id}/weight")
	public String postEditWeightReps(@ModelAttribute @Validated WeightExerciseDataForm form,
			BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			model.addAttribute("weightExerciseDataForm", form);

			return "training/exercise/edit/editWeightReps";
		}
		redirectAttributes.addAttribute("id", form.getId());
		String formattedDate = form.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		redirectAttributes.addAttribute("date", formattedDate);
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());
		redirectAttributes.addAttribute("reps", form.getReps());
		redirectAttributes.addAttribute("weight",form.getWeight());

		return "redirect:/exercise/edit/{id}/weightCheck";
	}

	@GetMapping("/edit/{id}/weightCheck")
	public String getEditWeightRepsCheck(@ModelAttribute ExerciseDataForm form) {
		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(form.getExerciseId());

		//筋トレ部位を設定
		form.setBodyPartName(record.getBodyParts().getName());
		//筋トレ種目を設定
		form.setExerciseName(record.getExercise().getName());

		return "training/exercise/edit/editWeightRepsCheck";
	}

	@PostMapping("/edit/{id}/weightCheck")
	public String postEditWeightRepsCheck(@ModelAttribute ExerciseDataForm form) {
		//筋トレデータを更新
		exerciseService.updateExerciseRecordOne(form.getDate(), form.getBodyPartId(),
				form.getExerciseId(), form.getWeight(), form.getReps(), form.getId());

		return "redirect:/exercise";
	}

	//トレーニングデータを削除する画面へ遷移
	@GetMapping("/delete/{id}")
	public String getDelete(@ModelAttribute ExerciseDataForm form, Authentication authentication, Model model) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//特定の筋トレデータ取得
		ExerciseRecord record = exerciseService.showSpecificData(form.getId());

		// 現在のユーザーがこのデータにアクセスできるか確認
		if (record.getUserId() != (userDetails.getId())) {
			throw new AccessDeniedException("不正なアクセスです");
		}

		int weightBased = exerciseService.checkWeightBased(record.getExerciseId());

		form.setDate(record.getDate());

		form.setBodyPartName(record.getBodyParts().getName());

		form.setExerciseName(record.getExercise().getName());

		form.setWeight(record.getWeight());

		form.setReps(record.getReps());

		form.setId(record.getId());

		if (weightBased == 0) {
			//重量がない場合
			return "training/exercise/edit/deleteReps";
		} else {
			//重量がある場合
			return "training/exercise/edit/deleteWeightReps";
		}
	}

	//重量あり筋トレデータ（１件）削除
	@PostMapping("/delete/{id}/weight")
	public String postDeleteWeightReps(@ModelAttribute ExerciseDataForm form, Authentication authentication,
			HttpSession session, Model model) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		//特定の筋トレデータ取得
		ExerciseRecord record = exerciseService.showSpecificData(form.getId());

		// 現在のユーザーがこのデータにアクセスできるか確認
		if (record.getUserId() != (userDetails.getId())) {
			throw new AccessDeniedException("不正なアクセスです");
		}

		//筋トレデータ削除
		exerciseService.deleteExerciseRecordOne(form.getId());

		//筋トレ記録画面にリダイレクト
		return "redirect:/exercise";
	}

	//重量なし筋トレデータ（１件）削除
	@PostMapping("/delete/{id}/reps")
	public String postDeleteReps(@ModelAttribute ExerciseDataForm form, Authentication authentication,
			HttpSession session, Model model) {
		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		//特定の筋トレデータ取得
		ExerciseRecord record = exerciseService.showSpecificData(form.getId());

		// 現在のユーザーがこのデータにアクセスできるか確認
		if (record.getUserId() != (userDetails.getId())) {
			throw new AccessDeniedException("不正なアクセスです");
		}

		//筋トレデータ削除
		exerciseService.deleteExerciseRecordOne(form.getId());

		//筋トレ記録画面にリダイレクト
		return "redirect:/exercise";
	}

	//種目を削除する画面を表示
	@GetMapping("/delete/types")
	public String deleteExercise(@ModelAttribute DeleteExerciseForm form, Authentication authentication, Model model) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList", bodyPartsList);
		
		if(form.getBodyPartId()!=null) {
			List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId(), userDetails.getId());
			model.addAttribute("exerciseList", exerciseList);
		}
		
		//種目を選択する画面を表示
		return "training/exercise/edit/deleteExercise";
	}

	//種目削除確認画面へ遷移
	@PostMapping("/delete/types")
	public String postDeleteExercise(@ModelAttribute @Validated DeleteExerciseForm form, BindingResult bindingResult,
			Authentication authentication, Model model,RedirectAttributes redirectAttributes) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		if (bindingResult.hasErrors()) {
			model.addAttribute("deleteExerciseForm", form);
			//部位を取得
			List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
			model.addAttribute("bodyPartsList", bodyPartsList);
			//部位は選択されていた場合
			if (form.getBodyPartId() != null) {
				//種目を取得
				List<Exercise> exerciseList = exerciseService.getExercises(form.getBodyPartId(), userDetails.getId());
				model.addAttribute("exerciseList", exerciseList);
			}

			return "training/exercise/edit/deleteExercise";
		}
		
		redirectAttributes.addAttribute("exerciseId", form.getExerciseId());

		return "redirect:/exercise/delete/typesCheck";
	}

	//種目削除確認画面を表示
	@GetMapping("/delete/typesCheck")
	public String deleteExerciseCheck(@ModelAttribute DeleteExerciseForm form) {

		//筋トレ種目と種目ID、筋トレ部位を一件取得
		ExerciseRecord record = exerciseService.getOneExercise(form.getExerciseId());

		//sessionFormに筋トレ部位名を設定
		form.setBodyPartName(record.getBodyParts().getName());

		//sessionFormに筋トレ種目名を設定
		form.setExerciseName(record.getExercise().getName());

		return "training/exercise/edit/deleteExerciseCheck";
	}

	//種目が削除され、ダッシュボード画面に遷移する
	@PostMapping("delete/typesCheck")
	public String postDeleteExerciseCheck(@ModelAttribute DeleteExerciseForm form) {
		//筋トレ種目を種目IDで論理削除する
		exerciseService.softDeleteExercise(form.getExerciseId());

		return "redirect:/exercise";
	}

	//種目を追加する画面を表示
	@GetMapping("/add/types")
	public String addExercise(@ModelAttribute AddExerciseForm form, Authentication authentication, Model model) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		//部位を取得
		List<BodyParts> bodyPartsList = exerciseService.getBodyParts();
		model.addAttribute("bodyPartsList", bodyPartsList);

		if (form.getBodyPartId() != null) {
			// 部位が選択されている場合、その部位に紐づく種目リストを取得
			List<Exercise> exercises = exerciseService.getExercises(form.getBodyPartId(), userDetails.getId());

			if (exercises == null || exercises.isEmpty()) {
				// 種目がnullまたは空リストの場合
				model.addAttribute("message", "・登録されている種目がありません。");
			} else {
				model.addAttribute("exercises", exercises);
			}
		} else {
			// bodyPartIdがnullの場合、部位が選択されていないメッセージを表示
			model.addAttribute("message", "・部位を選択してください");
		}

		//種目を追加する画面を表示
		return "training/exercise/edit/addExercise";
	}

	//種目追加確認画面へ遷移
	@PostMapping("/add/types")
	public String postAddExercise(@ModelAttribute @Validated AddExerciseForm form, BindingResult bindingResult,
			Authentication authentication, Model model, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			return addExercise(form, authentication, model);
		}

		if (form.getWeightBased() == 1) {
			form.setWeightBasedText("あり");
		} else if (form.getWeightBased() == 0) {
			form.setWeightBasedText("なし");
		}
		
		redirectAttributes.addAttribute("bodyPartId", form.getBodyPartId());
		
		redirectAttributes.addAttribute("exerciseName", form.getExerciseName());
		
		redirectAttributes.addAttribute("weightBased", form.getWeightBased());
		
		redirectAttributes.addAttribute("weightBasedText", form.getWeightBasedText());

		return "redirect:/exercise/add/typesCheck";
	}

	//種目追加確認画面を表示
	@GetMapping("/add/typesCheck")
	public String addExerciseCheck(@ModelAttribute AddExerciseForm form) {

		BodyParts bodyPart = exerciseService.getOneBodyPart(form.getBodyPartId());

		form.setBodyPartName(bodyPart.getName());

		return "training/exercise/edit/addExerciseCheck";
	}

	//種目が追加され、種目選択画面に移動
	@PostMapping("/add/typesCheck")
	public String postExerciseCheck(@ModelAttribute AddExerciseForm form,Authentication authentication) {

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

		exerciseService.addExercise(form.getExerciseName(), form.getBodyPartId(),
				userDetails.getId(), form.getWeightBased());

		return "redirect:/exercise/add/event";
	}
}
