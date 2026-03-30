package com.ktdsuniversity.edu.members.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ktdsuniversity.edu.members.dao.MembersDao;
import com.ktdsuniversity.edu.members.vo.MembersVO;
import com.ktdsuniversity.edu.members.vo.request.RegistVO;
import com.ktdsuniversity.edu.members.vo.request.UpdateVO;

@Service
public class MembersServiceImpl implements MembersService {

	@Autowired
	private MembersDao membersDao;
	
	@Override
	public boolean createNewMember(RegistVO registVO) {
		int insertCount = this.membersDao.insertNewMember(registVO);
		return insertCount == 1;
	}

	@Override
	public MembersVO findMemberByEmail(String email) {
		MembersVO searchResult = this.membersDao.selectMemberByEmail(email);
		return searchResult;
	}

	@Override
	public boolean updateMemberByEmail(UpdateVO updateVO) {
		int updateCount = this.membersDao.updateMemberByEmail(updateVO);
		return updateCount == 1;
	}

	@Override
	public boolean deleteMemberByEmail(String email) {
		int deleteCount = this.membersDao.deleteMemberByEmail(email);
		return deleteCount == 1;
	}

}
