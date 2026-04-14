package com.ktdsuniversity.edu.security.user;

import java.util.Collection;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ktdsuniversity.edu.members.vo.MembersVO;

/**
 * Spring Security가 사용자를 식별할 때 사용
 */
public class SecurityUser implements UserDetails {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1916873876236024863L;
	
	/**
	 * UserDetails 인터페이스로 사용자의 세부 내용을 알 수 없기 때문에
	 * 사용자의 정보를 가지고 있는 memberVO를 멤버변수로 추가해준다
	 */
	private MembersVO membersVO;
	
	public SecurityUser(MembersVO membersVO) {
		this.membersVO = membersVO;
	}
	
	public MembersVO getMembersVO() {
		return this.membersVO;
	}

	/**
	 * 사용자의 권한 목록을 관리
	 * 추후 권한별 서비스 제공시 사용
	 * GrantedAuthority < 사용자에게 허용된 권한
	 */
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	/**
	 * 로그인 한 회원의 비밀번호
	 */
	@Override
	public @Nullable String getPassword() {
		return this.membersVO.getPassword();
	}

	/**
	 * 사용자의 아이디(식별가능한)	
	 */
	@Override
	public String getUsername() {
		return this.membersVO.getEmail();
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.membersVO.getBlockYn().equals("N");
	}

}
