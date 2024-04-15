package com.example.complier;

import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.net.URLClassLoader;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Compiler {

	@Value("${file.path}")
	private String filePath;

	@Value("${file.filename}")
	private String filename;

	@Value("${file.main}")
	private String main;

	@Value("${file.java-extension}")
	private String javaExtension;

	public Object compile(String code) throws Exception {
		createDirectoryAndFile(code);
		return compileSourceAndLoadClass(filePath + filename + javaExtension, filePath + main + javaExtension);
	}

	private void createDirectoryAndFile(String code) {
		File directory = new File(filePath);
		if (!directory.exists()) {
			boolean isDirectoryCreated = directory.mkdir();
			if (!isDirectoryCreated) {
				throw new IllegalArgumentException("디렉터리가 없습니다.");
			}
		}

		File javaFile = new File(filePath + filename + javaExtension);
		try {
			if (!javaFile.createNewFile()) {
				throw new IllegalArgumentException("해당 파일이 없습니다.");
			}
			FileWriter writer = new FileWriter(javaFile);
			writer
				.append(code)
				.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Object compileSourceAndLoadClass(String... javaSourceFilePaths) throws Exception {
		JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
		int compileResult = javaCompiler.run(null, null, null, javaSourceFilePaths);

		if (compileResult == 1) {
			throw new IllegalArgumentException("컴파일에 실패했습니다.");
		}

		File directory = new File(filePath);
		if (!directory.exists()) {
			throw new IllegalArgumentException("디렉터리가 존재하지 않습니다.");
		}
		URL url = directory.toURI().toURL();

		try(URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{url})){
			return urlClassLoader.loadClass(filename).getDeclaredConstructor().newInstance();
		}
	}
}
