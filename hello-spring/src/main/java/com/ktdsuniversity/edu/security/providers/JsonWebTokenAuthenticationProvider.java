package com.ktdsuniversity.edu.security.providers;

import java.time.Duration;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * 사용자의 정보를 이용해 인증 객체를 생성하고 검증하는 클래스
 * Spring Security AuthenticationProvider와는 무관
 * 사용목적 : API를 호출할 때 인증수단으로 사용하기 위해
 */
public class JsonWebTokenAuthenticationProvider {
	
	
	private String secretKey;
	private String issuer;
	
	
	public JsonWebTokenAuthenticationProvider(String secretKey, String issuer) {
		this.secretKey = secretKey;
		this.issuer = issuer;
	}

	/**
	 * 사용자가 요청할 때마다 Request Header[Authorization]에 전달한
	 * JsonWebToken을 가져와서 복호화 시킨다.
	 * 복호화된 결과에서 사용자의 이메일(identify)을 추출하여 반환 시킨다.
	 * 
	 * @param jsonWebToken 사용자가 전달한 토큰
	 * @return jsonWebToken에서 추출한 사용자의 이메일
	 */
	public String decryptJsonWebToken(String jsonWebToken) {

		// 암/복호화 키 생성
		SecretKey signKey = Keys.hmacShaKeyFor(this.secretKey.getBytes());
		
		Claims claims = Jwts.parser() // JsonWebToken을 분석하기 위한 선언
							.verifyWith(signKey) // JsonWebToken을 복호화 하기 위한 비밀키 지정
							.requireIssuer(this.issuer) // 사용자가 전달한 JsonWebToken이 hello-spring 시스템에서 만든것인지 확인한다(최소한의 검증)
							.build() // JsonWebToken을 복호화 시작
							.parseSignedClaims(jsonWebToken) // 사용자가 전달한 JsonWebToken을 복호화 한다
							.getPayload(); // 복호화 된 결과에서 claim들만 모아 반환시킨다. (Map의 형태)
		// 사용자가 전달한 JsonWebToken을 복호화 한 뒤 identify 값을 추출한다
		String email = claims.get("identify", String.class);
		
		return email;
	}
	
	/**
	 * 사용자의 이메일을 이용해 인증용 JWT를 생성하고
	 * 결과를 사용자에게 보내주어야한다.
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
		SecretKey signKey = Keys.hmacShaKeyFor(this.secretKey.getBytes());
		
		String jsonWebToken = Jwts.builder()
								  //JsonWebToken을 발행한 시스템의 이름
								  .issuer(this.issuer)
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
	
	public static void main(String[] args) {
		JsonWebTokenAuthenticationProvider jwtProvider = new JsonWebTokenAuthenticationProvider("qwepuon2134GXChj2rhsandpozxcvasdlkhaslaAlkj","hello-spring");
		String jwt = jwtProvider.makeJsonWebToken("test@test.com", Duration.ofMillis(10000));
		System.out.println(jwt);
		
		//복호화 진행
		String email = jwtProvider.decryptJsonWebToken(jwt);
		System.out.println(email);
		
	}
	
	

}
