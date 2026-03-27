package com.ktdsuniversity.edu.members.dao;

import org.apache.ibatis.annotations.Mapper;

import com.ktdsuniversity.edu.members.vo.request.RegistVO;

@Mapper
public interface MembersDao {

	int insertNewMember(RegistVO registVO);

}
