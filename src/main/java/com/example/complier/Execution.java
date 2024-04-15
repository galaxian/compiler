package com.example.complier;

import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.stereotype.Component;

@Component
public class Execution {

	private static final long TIMEOUT = 10000;

	public static Map<String, Object> runObject(Object obj, Object[] params) {
		Map<String, Object> resultMap = new HashMap<>();
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		ByteArrayOutputStream errStream = new ByteArrayOutputStream();
		PrintStream origOut = System.out;
		PrintStream origErr = System.err;

		try {
			System.setOut(new PrintStream(outStream));
			System.setErr(new PrintStream(errStream));

			// 메서드를 실행합니다.
			resultMap = executeMethod(obj, params);
		} finally {
			System.setOut(origOut);
			System.setErr(origErr);
		}

		resultMap.put("stdout", new String(outStream.toByteArray(), StandardCharsets.UTF_8));
		resultMap.put("stderr", new String(errStream.toByteArray(), StandardCharsets.UTF_8));

		return resultMap;
	}

	private static Map<String, Object> executeMethod(Object obj, Object[] args) {
		Map<String, Object> resultMap = new HashMap<>();

		try {
			resultMap = timeOutCall(obj, args);
			resultMap.put("result", "무사히 메서드 실행 성공");
		} catch (TimeoutException e) {
			resultMap.put("result", "시간 초과로 인한 실패");
			resultMap.put("error", "Timeout occurred");
		} catch (Exception e) {
			resultMap.put("result", "시간 초과 이외로 인한 실패");
			resultMap.put("error", e.getMessage());
		}

		return resultMap;
	}

	private static Map<String, Object> timeOutCall(Object obj, Object[] args) throws Exception {
		Map<String, Object> resultMap;
		ExecutorService executorService = Executors.newSingleThreadExecutor();

		Method method = obj.getClass().getMethod("caller", String[].class);

		try {
			Callable<Map<String, Object>> task = () -> {
				Map<String, Object> result = new HashMap<>();
				try {
					result.put("return", method.invoke(obj, (Object) args));
					result.put("result", "동작 성공");
				} catch (Exception e) {
					result.put("result", "코드 동작 실패");
					result.put("error", e.getMessage());
				}
				return result;
			};

			Future<Map<String, Object>> future = executorService.submit(task);
			resultMap = future.get(TIMEOUT, TimeUnit.MILLISECONDS);
		} finally {
			executorService.shutdown();
		}

		return resultMap;
	}

}
