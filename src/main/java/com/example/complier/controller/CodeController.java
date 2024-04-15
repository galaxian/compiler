package com.example.complier.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.complier.dto.CodeRequestDto;
import com.example.complier.service.CodeService;

@RestController
public class CodeController {

	private final CodeService codeService;

	public CodeController(CodeService codeService) {
		this.codeService = codeService;
	}

	@PostMapping("codes")
	public ResponseEntity<Object> compileCode(@RequestBody CodeRequestDto requestDto) throws Exception {
		Object compile = codeService.solve(requestDto);
		return ResponseEntity.ok(compile);
	}
}
