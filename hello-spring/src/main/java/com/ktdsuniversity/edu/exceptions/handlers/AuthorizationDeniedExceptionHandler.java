package com.ktdsuniversity.edu.exceptions.handlers;

import java.io.IOException;
import java.io.PrintWriter;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.ktdsuniversity.edu.common.utils.AuthUtils;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


public class AuthorizationDeniedExceptionHandler  implements AccessDeniedHandler{

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException, ServletException {

		Boolean isApiRequest = request.getServletPath().startsWith("/api/");
		// AuthorizationDeniedException이 발생한 위치가 "/api/"라면 JSON으로 에러메시지를 전달
		if(isApiRequest) {
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");

			PrintWriter writer = response.getWriter();
			writer.append("{ \"error\" : \"인증이 필요하거나 잘못된 권한입니다.\" }");
			writer.flush();
			return;
		}
		// AuthorizationDeniedException이 발생한 위치가 "/api/"가 아니라면(JSP Template) ErrorPage 전달
		else {
			String viewPath = "/WEB-INF/views/members/login.jsp";
			if(AuthUtils.isAuthenticated()) {
				viewPath = "/WEB-INF/views/errors/403.jsp";
				request.setAttribute("errorMessage","잘못된 접근입니다. 권한이 충분하지 않습니다");
				RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
				requestDispatcher.forward(request, response);
				return;
			}
			RequestDispatcher requestDispatcher = request.getRequestDispatcher(viewPath);
			requestDispatcher.forward(request, response);
			return;
		}
	}

}
