package com.example.complier.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.complier.Compiler;
import com.example.complier.Execution;
import com.example.complier.dto.CodeRequestDto;

@Service
public class CodeService {

	private final Compiler compiler;

	public CodeService(Compiler compiler) {
		this.compiler = compiler;
	}

	public Object solve(CodeRequestDto requestDto) throws Exception {
		Object compileCode = compiler.compile(requestDto.getCode());

		long startTime = System.currentTimeMillis();

		String[] args = {"1", "2", "3", "4", "5"};
		Map<String, Object> result = Execution.runObject(compileCode, args);

		long endTime = System.currentTimeMillis();

		return result;
	}
}
