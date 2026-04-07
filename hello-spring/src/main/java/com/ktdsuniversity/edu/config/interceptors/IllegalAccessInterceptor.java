package com.ktdsuniversity.edu.config.interceptors;

import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Endpoint에 접근하기 전, 세션이 존재할 경우
 * 컨트롤러 실행되지 않고 게시글 목록 조회 페이지로 이동 시킨다.
 * preHandle, postHandle, afterCompletion 중 하나만 override해 구현.
 * 
 * 페이지 이동 코드
 * response.sendRedirect("URL");
 */
public class IllegalAccessInterceptor
	implements HandlerInterceptor {

	@Override
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler)
			throws Exception {
		
		HttpSession session = request.getSession();
		if (session.getAttribute("__LOGIN_DATA__") != null) {
			response.sendRedirect("/");
			return false;
		}
		
		return true;
	}
	
}
