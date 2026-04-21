package com.ktdsuniversity.edu.files.vo.response;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

public class DownloadVO {

	private String displayName;
	private String extendName;
	private long fileLength;
	private String filePath;

	// 사용자에게 전달해줄 파일 객체.
	private File file;

	// 브라우저에게 전달하기 위한 파일 객체
	private Resource resource;

	public String getDisplayName() {
		return this.displayName;
	}

	public void setDisplayName(String displayName) {
		// Java 기반 애플리케이션에서 파일을 다운로드할 때
		// 영어를 제외한 글자들이 사라지는 현상.
		// ==> 사라지지 않도록 다국어 지원.
		this.displayName = displayName;

		try {
			this.displayName = URLEncoder.encode(displayName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}
	}

	public String getExtendName() {
		return this.extendName;
	}

	public void setExtendName(String extendName) {
		this.extendName = extendName;
	}

	public long getFileLength() {
		return this.fileLength;
	}

	public void setFileLength(long fileLength) {
		this.fileLength = fileLength;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
		// this.file 생성
		this.file = new File(this.filePath);
		// this.resource 생성.
		try {
			FileInputStream fileStream = new FileInputStream(this.file);
			this.resource = new InputStreamResource(fileStream);
		}
		catch(FileNotFoundException fnfe) {
			// TODO 전용 예외 발생시켜 던지기
		}
	}

	public File getFile() {
		return this.file;
	}

	public Resource getResource() {
		return this.resource;
	}

}
