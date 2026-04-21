package com.ktdsuniversity.edu.security.authenticate.filters;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ktdsuniversity.edu.common.utils.StringUtils;
import com.ktdsuniversity.edu.security.providers.JsonWebTokenAuthenticationProvider;
import com.ktdsuniversity.edu.security.user.SecurityUser;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 이 클래스의 객체가 Security Filter Chain에 등록되어 인증이 필요한 모든 엔드포인트가 실행되기 전에
 * AuthenticationToken(UsernamePasswordAuthenticationToken)을 생성하도록 하는 필터
 *
 * HttpServletRequest의 header로 전달된 Authorization에 들어있는
 * JWT를 가져와 분석(복호화 및 검증)을 진행하고 분석된 결과를 AuthenticationToken으로 생성시킨다.
 */
public class JsonWebTokenAuthenticationFilter extends OncePerRequestFilter{

	private JsonWebTokenAuthenticationProvider jsonWebTokenAuthenticationProvider;
	private UserDetailsService userDetailsService;

	public JsonWebTokenAuthenticationFilter(JsonWebTokenAuthenticationProvider jsonWebTokenAuthenticationProvider,
			UserDetailsService userDetailsService) {
		this.jsonWebTokenAuthenticationProvider = jsonWebTokenAuthenticationProvider;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		//다음필터가 동작되기 이전의 이 필터가 해야할일 작성

		// Authorization 이 존재하는지 확인하는 조건
		// 요청 URL 가져오기
		String requestURI = request.getServletPath();
		// 요청 URL이 "/api/"로 시작하는 경우에만 실행
		if(requestURI.startsWith("/api/")) {
			// Request에서 header에 있는 Authorization을 꺼내온다
			String jsonWebToken = request.getHeader("Authorization");

			if(!StringUtils.isEmpty(jsonWebToken)) {

				// JWT를 복호화 시켜 email을 가져온다
				String email = null;
				try {
					email = this.jsonWebTokenAuthenticationProvider.decryptJsonWebToken(jsonWebToken);
				}
				catch (JwtException je) {
					response.setCharacterEncoding("UTF-8");
					response.setContentType("application/json");

					PrintWriter writer;
					writer = response.getWriter();
					writer.append("{ \"error\": \"인증이 필요하거나 잘못된 권한입니다.\" }");
					writer.flush();
					return;
				}

				// email을 이용해 사용자의 정보와 권한을 조회한다.
				UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
				SecurityUser securityUser = (SecurityUser) userDetails;

				// 사용자의 정보를 이용해 AuthenticationToken(UsernamePasswordAuthenticationToken)을 발행한다.
				Authentication authToken = new UsernamePasswordAuthenticationToken(securityUser.getMembersVO(), userDetails.getPassword(), userDetails.getAuthorities());

				//발행한 AuthenticationToken을 SecurityContext에 적재한다
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}

		}


		filterChain.doFilter(request, response); // 이 다음필터가 있다면 그 필터를 동작시킨다.

		//모든필터가 동작이 완료되고 Filter Chain의 역순으로 응답이 돌아올 때 이 필터가 해야할일 작성
	}

}
