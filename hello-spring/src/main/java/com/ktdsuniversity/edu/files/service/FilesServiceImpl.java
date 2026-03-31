package com.ktdsuniversity.edu.files.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktdsuniversity.edu.files.dao.FilesDao;

@Service
public class FilesServiceImpl implements FilesService {

	@Autowired
	private FilesDao filesDao;
	
}
