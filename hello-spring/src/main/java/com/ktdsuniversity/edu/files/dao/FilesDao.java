package com.ktdsuniversity.edu.files.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ktdsuniversity.edu.board.vo.request.UpdateVO;
import com.ktdsuniversity.edu.files.vo.request.SearchFileVO;
import com.ktdsuniversity.edu.files.vo.request.UploadVO;
import com.ktdsuniversity.edu.files.vo.response.DownloadVO;

@Mapper
public interface FilesDao {

	int insertAttachFile(UploadVO uploadVO);

	DownloadVO selectFilesByFileGroupIdAndFileNum(SearchFileVO searchFileVO);

	List<String> selectFilePathByFileGroupIdAndFileNums(UpdateVO updateVO);

	int deleteFilesByFileGroupIdAndFileNums(UpdateVO updateVO);

	List<String> selectFilePathByFileGroupId(String id);

	int deleteFileByFileGroupId(String id);

	String selectNewFileGroupId();

	int insertFileGroupId(String fileGroupId);

}
