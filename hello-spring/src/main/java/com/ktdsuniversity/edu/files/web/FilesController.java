package com.ktdsuniversity.edu.files.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ktdsuniversity.edu.files.service.FilesService;
import com.ktdsuniversity.edu.files.vo.request.SearchFileVO;
import com.ktdsuniversity.edu.files.vo.response.DownloadVO;

@Controller
public class FilesController {

	@Autowired
	private FilesService filesService;

	private Map<String, String> mimeTypeMap;

	public FilesController() {
		this.mimeTypeMap = new HashMap<>();
		this.mimeTypeMap.put("txt", "text/plain");
		// Images
		this.mimeTypeMap.put("jpg", "image/jpeg");
		this.mimeTypeMap.put("jpeg", "image/jpeg");
		this.mimeTypeMap.put("png", "image/png");
		this.mimeTypeMap.put("webp", "image/webp");
		this.mimeTypeMap.put("gif", "image/gif");
		this.mimeTypeMap.put("svg", "image/svg");

		// Static Resources
		this.mimeTypeMap.put("css", "text/css");
		this.mimeTypeMap.put("js", "text/javascript");
		this.mimeTypeMap.put("html", "text/html");

		// MS Office
		this.mimeTypeMap.put("csv", "text/csv");
		this.mimeTypeMap.put("xls", "application/vnd.ms-excel");
		this.mimeTypeMap.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		this.mimeTypeMap.put("ppt", "application/vnd.ms-powerpoint");
		this.mimeTypeMap.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");

		// Archive
		this.mimeTypeMap.put("zip", "application/zip");

		// Document
		this.mimeTypeMap.put("pdf", "application/pdf");
	}

	@GetMapping("/file/{fileGroupId}/{fileNum}")
	public ResponseEntity<Resource> doDownloadAction(
			@PathVariable String fileGroupId,
			@PathVariable int fileNum) {

		SearchFileVO searchFileVO = new SearchFileVO();
		searchFileVO.setFileGroupId(fileGroupId);
		searchFileVO.setFileNum(fileNum);

		// 다운로드를 위한 정보와 파일 찾아오기
		DownloadVO downloadVO = this.filesService.findAttachFile(searchFileVO);

		// 다운로드 시작
		// HTTP Response 셋팅.
		// HTTP Response Header 세팅.
		HttpHeaders headers = new HttpHeaders();
		// Content-Disposition : 다운로드할 파일의 이름 작성
		headers.set(HttpHeaders.CONTENT_DISPOSITION,
					"attachment; filename=" + downloadVO.getDisplayName());
		// Content-Length : 다운로드할 파일의 크기(bytes) 작성
		headers.set(HttpHeaders.CONTENT_LENGTH,
					downloadVO.getFileLength() + "");
		// Content-Type : 다운로드할 파일의 마임타입(Mime-Type) 작성.
		headers.set(HttpHeaders.CONTENT_TYPE,
					this.mimeTypeMap.getOrDefault(
							downloadVO.getExtendName().toLowerCase(),
							"application/octet-stream"));

		// 브라우저에게 Http Response 전송.
		return ResponseEntity.ok()
							 .headers(headers)
							 .body(downloadVO.getResource());
	}

}
