package com.ktdsuniversity.edu.files.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ktdsuniversity.edu.files.service.FilesService;

@Controller
public class FilesController {

	@Autowired
	private FilesService filesService;
	
}
