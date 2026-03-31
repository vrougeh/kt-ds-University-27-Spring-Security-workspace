package com.ktdsuniversity.edu.members.vo.response;

import java.util.List;

import com.ktdsuniversity.edu.members.vo.MembersVO;

/**
 * 게시글 검색 결과를 담고 있는 클래스. 
 * 게시글 목록 
 * 게시글 개수
 */
public class SearchResultVO {

	private List<MembersVO> result;
	private int count;

	public List<MembersVO> getResult() {
		return this.result;
	}

	public void setResult(List<MembersVO> result) {
		this.result = result;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
