package com.ktdsuniversity.edu.security.providers;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * 사용자의 정보를 이용해 인증 객체를 생성하고 검증하는 클래스
 * Spring Security AuthenticationProvider와는 무관
 * 사용목적 : API를 호출할 때 인증수단으로 사용하기 위해
 */
public class JsonWebTokenAuthenticationProvider {
	
	/**
	 * 사용자의 이메일을 이용해 인증용 JWT를 생성
	 * 
	 * @param email 사용자의 이메일
	 * @param expiredAt JWT의 유효 기간(지금부터 ~분(시간,일,월,연) 까지 유효)
	 * @return email과 expiredAt으로 생성한 JsonWebToken
	 */
	public String makeJsonWebToken(String email, Duration expiredAt) {
		
		// JsonWebToken이 발행되는 날짜와 시간을 생성
		Date issueDate = new Date();
		
		// JsonWebToken이 만료되는 날짜와 시간을 생성
		// 발행 날짜 시간 + expiredAt
		Date expirationDate = new Date(issueDate.getTime() + expiredAt.toMillis());
		
		// 암/복호화 키 생성
		// TODO application.yml에서 가져오기
		SecretKey signKey = Keys.hmacShaKeyFor("qwepuon2134GXChj2rhsandpozxcvasdlkhaslaAlkj".getBytes());
		
		String jsonWebToken = Jwts.builder()
								  //JsonWebToken을 발행한 시스템의 이름
								  // TODO application.yml에서 가져오기
								  .issuer("hello-spring")
								  //JsonWebToken의 이름
								  .subject(email+"_token")
								  //JsonWebToken에 포함되어야 할 회원의 정보들
								  .claim("identify", email)
								  //JsonWebToken을 발행한 날짜와 시간
								  .issuedAt(issueDate)
								  //JsonWebToken이 만료되는 날짜와 시간
								  .expiration(expirationDate)
								  //평문으로 구성된 JsonWebToken을 암호화 또는 복호화 시킬 때 사용할 키(Salt)
								  .signWith(signKey)
								  //Jwts에 제공된 데디터를 이용해 String Type의 Token을 생성
								  .compact();
		
		return jsonWebToken;
	}
	
//	public static void main(String[] args) {
//		JsonWebTokenAuthenticationProvider jwtProvider = new JsonWebTokenAuthenticationProvider();
//		String jwt = jwtProvider.makeJsonWebToken("test@test.com", Duration.ofHours(3));
//		System.out.println(jwt);
//		
//		
//	}

}
