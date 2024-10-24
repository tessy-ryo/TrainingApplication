package com.example.aspect;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(DataAccessException.class)
	public String dataAccessExceptionHandler(DataAccessException e,Model model) {
		
		model.addAttribute("error","");
		
		model.addAttribute("message","不正な操作が行われました");
		
		model.addAttribute("status",HttpStatus.INTERNAL_SERVER_ERROR);
		
		return "error";
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public String accessDeniedExceptionHandler(AccessDeniedException e, Model model) {
		
		model.addAttribute("error","");
		
		model.addAttribute("message","アクセス権限がありません");
		
		model.addAttribute("status",HttpStatus.FORBIDDEN);
		
		return "error";
	}
	
	@ExceptionHandler(Exception.class)
	public String exceptionHandler(Exception e,Model model) {
		
		model.addAttribute("error","");
		
		model.addAttribute("message","不正な操作が行われました");
		
		model.addAttribute("status",HttpStatus.INTERNAL_SERVER_ERROR);
		
		return "error";
	}
}
