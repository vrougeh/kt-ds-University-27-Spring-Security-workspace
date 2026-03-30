package com.ktdsuniversity.edu.members.service;

import com.ktdsuniversity.edu.members.vo.MembersVO;
import com.ktdsuniversity.edu.members.vo.request.RegistVO;
import com.ktdsuniversity.edu.members.vo.request.UpdateVO;

public interface MembersService {

	boolean createNewMember(RegistVO registVO);

	MembersVO findMemberByEmail(String email);

	boolean updateMemberByEmail(UpdateVO updateVO);

	boolean deleteMemberByEmail(String email);

}
