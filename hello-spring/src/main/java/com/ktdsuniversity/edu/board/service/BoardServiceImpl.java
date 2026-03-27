package com.ktdsuniversity.edu.board.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktdsuniversity.edu.board.dao.BoardDao;
import com.ktdsuniversity.edu.board.vo.BoardVO;
import com.ktdsuniversity.edu.board.vo.request.WriteVO;
import com.ktdsuniversity.edu.board.vo.response.SearchResultVO;

@Service
public class BoardServiceImpl implements BoardService {

	/**
	 * 빈 컨테이너에 들어있는 객체 중 타입이 일치하는 객체를 할당 받는다.
	 */
	@Autowired
	private BoardDao boardDao;
	
	@Override
	public SearchResultVO findAllBoard() {
		// 게시글 개수 조회. ==> 1
		int count = this.boardDao.selectBoardCount();
		
		// 게시글 목록 조회. ==> [BoardVO]
		List<BoardVO> list = this.boardDao.selectBoardList();
		
		SearchResultVO result = new SearchResultVO();
		result.setResult(list);
		result.setCount(count);
		
		return result;
	}

	@Override
	public boolean createNewBoard(WriteVO writeVO) {
		// dao => insert 요청
		// mybatis 는 insert, update, delete를 수행했을 때
		// 영향을 받은 row의 수를 반환시킨다.
		// 예> insert ==> insert 된 row의 개수 반환.
		//     update ==> update 된 row의 개수 반환.
		//     delete ==> delete 된 row의 개수 반환.
		int insertCount = this.boardDao.insertNewBoard(writeVO);
		System.out.println("생성된 게시글의 개수? " + insertCount);
		return insertCount == 1;
	}

}
