package com.ktdsuniversity.edu.security.authenticate.oauth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ktdsuniversity.edu.members.dao.MembersDao;
import com.ktdsuniversity.edu.members.vo.MembersVO;
import com.ktdsuniversity.edu.members.vo.request.OAuthMemberVO;
import com.ktdsuniversity.edu.members.vo.request.RegistVO;


public class HelloSpringOAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User>{

	private static final Logger logger = LoggerFactory.getLogger(HelloSpringOAuthService.class);
	
	
	private MembersDao membersDao;
	
	
	public HelloSpringOAuthService(MembersDao membersDao) {
		this.membersDao = membersDao;
	}

	/**
	 * /oauth2/authorization/naver or google 을 통해 로그인 한 이후 수행되는 메소드
	 * naver 또는 google에서 redirect-uri로 응답을 돌려줄 때 실행된다
	 * @param userRequest oauth 서비스 제공자(naver,google)에게 개인정보를 요청하는 객체
	 *        1. authorization-uri 호출해서 oauth 인증 수행
	 *        2. 인증 성공 후 token-uri 호출해서 oauth token을 발급받는다
	 *        3. 발급받은 oauth token을 이용해서 user-info-uri를 호출해서 사용자 정보를 취득한다.
	 * @return OAuth2User 서비스 제공자(naver,google)로 부터 취득한 사용자의 정보를 이용해 Security 인증정보 생성
	 */
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		
		// userRequest를 통해서 개인정보 취득하기
		// OAuth2UserService의 기본 객체를 생성 후 userRequest 전달
		OAuth2UserService<OAuth2UserRequest, OAuth2User> userService = new DefaultOAuth2UserService();
		
		OAuth2User oathResult = userService.loadUser(userRequest);
		logger.debug(oathResult.toString());
		
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		
		MembersVO oAuthMember = new MembersVO();
		OAuth2User oAuth2Principal = null;
		
		if("naver".equals(registrationId)) {
			// OAuth 전용 인증 객체 생성해서 반환
			oAuth2Principal = new NaverOAuthUserDetails(oAuthMember, oathResult.getAttributes());
		} 
		else if("google".equals(registrationId)) {
			oAuth2Principal = new GoogleOAuthUserDetails(oAuthMember, oathResult.getAttributes());
		}
		
		//OAUTH 회원의 정보를 DB에 Insert한다.
		//이미 존재하는 회원이라면 insert하지 않도록 한다.
		if(oAuthMember != null) {
			boolean isGuest = this.membersDao.selectMemberByEmail(oAuthMember.getEmail()) == null;
			
			if(isGuest) {
				RegistVO registVO = new RegistVO();
				registVO.setEmail(oAuthMember.getEmail());
				registVO.setName(registrationId +"_" + oAuthMember.getName());
				registVO.setPassword("NONE");
				registVO.setSalt("NONE");
				this.membersDao.insertNewMember(registVO);
			}
			OAuthMemberVO oAuthMemberVO = new OAuthMemberVO();
			oAuthMemberVO.setEmail(oAuthMember.getEmail());
			oAuthMemberVO.setRegistrationId(registrationId);
			oAuthMemberVO.setName(oAuthMember.getName());
			boolean isNewOAuth = this.membersDao.selectOAuthMemberByEmailAndRegistrationId(oAuthMemberVO.getEmail()) == null;
			if(isNewOAuth) {
				this.membersDao.insertNewOAuthMember(oAuthMemberVO);
			}
		}
		
		logger.debug(oathResult.toString());
		
		return oAuth2Principal;
	}
	
}
