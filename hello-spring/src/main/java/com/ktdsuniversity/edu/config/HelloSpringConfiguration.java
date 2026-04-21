package com.ktdsuniversity.edu.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.ktdsuniversity.edu.exceptions.handlers.AuthorizationDeniedExceptionHandler;
import com.ktdsuniversity.edu.members.dao.MembersDao;
import com.ktdsuniversity.edu.security.authenticate.filters.JsonWebTokenAuthenticationFilter;
import com.ktdsuniversity.edu.security.authenticate.handlers.LoginFailureHandler;
import com.ktdsuniversity.edu.security.authenticate.handlers.LoginSuccessHandler;
import com.ktdsuniversity.edu.security.authenticate.oauth.HelloSpringOAuthService;
import com.ktdsuniversity.edu.security.authenticate.service.SecurityPasswordEncoder;
import com.ktdsuniversity.edu.security.authenticate.service.SecurityUserDetailsService;
import com.ktdsuniversity.edu.security.providers.JsonWebTokenAuthenticationProvider;
import com.ktdsuniversity.edu.security.providers.UsernameAndPasswordAuthenticationProvider;


// application.yml에서 작성할 수 없는 설정들을 적용하기 위한 Annotation
// @Component 의 자식 Annotation
@Configuration
// spring-boot-starter-validation 동작 활성화 시키기
// @EnableWebMvc가 추가되면 application.yml의 mvc 관련 설정들이 모두 무시된다.
//   1. spring.mvc.view.prefix, spring.mvc.view.suffix
//   2. src/main/resources/static 경로 사용 불가능.
@EnableWebMvc
// 생략 가능
// Spring Security 라이브러리를 활성화 시킨다.
// Spring Security의 필터 목록을 확인하기 위해서 작성한다.
@EnableWebSecurity(debug = true)
//controller 또는 service 코드에서 권한 검사를 수행하기 위한 어노테이션 추가
@EnableMethodSecurity
public class HelloSpringConfiguration implements
		// WebMvc 설정을 위한 Configuration
		// @EnableWebMvc Annotation 에서 적용하는 기본 설정들을 변경하기 위함.
		WebMvcConfigurer {

	@Autowired(required = false)
	@Lazy
	private MembersDao membersDao;

	//application.yml에서 관련된 정보를 가져온다
	// @Value가 동작하는 조건 : @Component가 적용된 클래스에서만 가능(@Component 의 자식 Annotation인 @Configuration이 선언 되어있음)
	@Value("${app.jwt.secret-key}") // 환경 설정 정보를 Bean으로 가져오는 방법 괄호에 환경 설정 경로를 작성
	private String jwtSecretKey;
	@Value("${app.jwt.issuer}")
	private String jwtIssuer;

	@Bean
	JsonWebTokenAuthenticationProvider createJwtAuthenticationProvider() {
		return new JsonWebTokenAuthenticationProvider(this.jwtSecretKey, this.jwtIssuer);
	}

	//SecurityPasswordEncoder의 Bean을 생성한다.
	@Bean // 메소드가 실행 되어서 반환되는 객체를 Bean Container에 적재한다
	PasswordEncoder createPasswordEncoder() {
		return new SecurityPasswordEncoder();
	}
	//SecurityUserDetailsService의 Bean을 생성한다.
	//@Bean으로 생성하는 객체(Bean)들은 필요한 의존 객체를 생성자로 주입해 주어야 한다.
	@Bean
	UserDetailsService createUserDetailsService() {
		return new SecurityUserDetailsService(this.membersDao);
	}
	//UserNameAndPasswordAuthenticationProvider의 Bean을 생성한다.
	@Bean
	AuthenticationProvider createAuthenticationProvider() {

		UserDetailsService userDetailsService = this.createUserDetailsService();
		PasswordEncoder passwordEncoder = this.createPasswordEncoder();

		return new UsernameAndPasswordAuthenticationProvider(userDetailsService, passwordEncoder);
	}


	@Bean
	AuthenticationSuccessHandler createLoginSuccessHandler() {
		return new LoginSuccessHandler(this.membersDao);
	}

	@Bean
	AuthenticationFailureHandler createLoginFailureHandler() {
		return new LoginFailureHandler(this.membersDao);
	}

	@Bean
	OncePerRequestFilter createJwtAuthFilter() {
		return new JsonWebTokenAuthenticationFilter(this.createJwtAuthenticationProvider(), this.createUserDetailsService());
	}
	
	@Bean
	OAuth2UserService<OAuth2UserRequest, OAuth2User> createOAuth2UserService(){
		return new HelloSpringOAuthService(this.membersDao);
	}


	/**
	 * 특정 URL에 대해서 SpringSecurity가 개입하지 않도록 설정
	 * /WEB-INF/views/ 아래의 모든 jsp 파일들은 Spring Security의 간섭을 받지 않는다.
	 * controller에서 해당페이지를 노출하려 할 때 경로 사용시 인증된 사용자에게만 노출시키려는 하는경우가 존재할때 Spring Security가 개입하지 않도록 설정
	 */
	@Bean
	WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
				.requestMatchers("/WEB-INF/views/**");
	}

	// Spring Login Filter(BasicAuthenticationFilter) 등록
	// Spring Security의 기본 로그인 절차를 수정하는 작업
	@Bean
	SecurityFilterChain configureFilterChain(HttpSecurity httpSecurity) {
		
		httpSecurity.oauth2Login(oauth2 -> oauth2.loginPage("/login").defaultSuccessUrl("/")
												 .userInfoEndpoint(endpoint -> endpoint.userService(this.createOAuth2UserService())));

		// 상대방이 내 서버로 접속할 수 있도록 허용하기
		// 내 서버로 접속 가능한 안전한 URL 등록하기
		httpSecurity.cors(corsConfigurer -> {
			CorsConfigurationSource source = (httpServletRequest) -> {
				// 허용할 타 사이트의 도메인을 작성
				CorsConfiguration config = new CorsConfiguration();

				// 허용할 타사이트의 URL에서 요청하는 모든 접근(API)들을 허용하겠다.
				config.addAllowedOrigin("http://192.168.211.20:8080");
				// 허용할 타사이트의 요청 Method
				// http://192.168.211.20:8080 에서 POST로 요청되는 접근들만 허용
				config.addAllowedMethod("POST");
				config.addAllowedMethod("GET");
				// 허용할 타 사이트의 요청 HttpHeader
				// 모든 요청 HttpHeader를 허용하겠다
				config.addAllowedHeader("*");
				return config;
			};
			corsConfigurer.configurationSource(source);
		});

		// CSRF 수정, 댓글 등록 불가(Invalid CSRF token found for ...)
		// CSRF를 체크하는 SecurityFilter(CsrfFilter)를 무효화
//		httpSecurity.csrf(csrf -> csrf.disable());
		//API 통신에서는 CSRF를 체크하지 않도록 설정
		httpSecurity.csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"));

		// Custom Filter(JsonWebTokenAuthenticationFilter) 추가 (?filter 뒤에 내가 만든 filter를 넣어서 실행,?filter)
		httpSecurity.addFilterAfter(this.createJwtAuthFilter(), UsernamePasswordAuthenticationFilter.class);

		// AuthorizationDeniedExceptionHandler를 추가
		// Controller 코드 이하에서 @PreAuthorized() 검증에 실패하면 아래 설정에 등록한 Handler가 동작
		httpSecurity.exceptionHandling(exceptionHandling -> exceptionHandling.accessDeniedHandler(new AuthorizationDeniedExceptionHandler()));

		//UsernamePasswordAuthenticationFilter 수정
		httpSecurity.formLogin(formLogin ->
					//로그인 url 지정
					formLogin.loginPage("/login")
					//로그인 인증 처리(UsernameAndPasswordAuthenticationProvider가 실행된 end point) - login.jsp의 action
					.loginProcessingUrl("/login-provider")
					//로그인에 필요한 아이디 파라미터 이름을 "username"에서 "email"로 변경 -login.jsp에서 input의 name이 email임
					.usernameParameter("email")
					//로그인 성공시
					//this.membersDao.updateSuccessLogin(loginVO);
					.successHandler(this.createLoginSuccessHandler())
					//로그인 실패시
					//this.membersDao.updateIncreaseLoginFailCount(loginVO.getEmail());
					//this.membersDao.updateBlock(loginVO.getEmail());
					.failureHandler(this.createLoginFailureHandler())
				);

		return httpSecurity.build();
	}

	// configureViewResolvers 설정
	//  spring.mvc.view.prefix, spring.mvc.view.suffix 재설정
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/views/", ".jsp");
	}

	// addResourceHandlers
	//  src/main/resources/static 경로의 endpoint 재설정
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// /static/css/ 폴더에 있는 파일들에 대한 Endpoint 설정.
		registry.addResourceHandler("/css/**") // /static/css/ 의 엔드포인트
				.addResourceLocations("classpath:/static/css/"); // /static/css/ 의 물리적인 위치

		// /static/image/ 폴더에 있는 파일들에 대한 Endpoint 설정.
		registry.addResourceHandler("/image/**") // /static/image/ 의 엔드포인트
				.addResourceLocations("classpath:/static/image/"); // /static/image/ 의 물리적인 위치

		// /static/js/ 폴더에 있는 파일들에 대한 Endpoint 설정.
		registry.addResourceHandler("/js/**") // /static/js/ 의 엔드포인트
				.addResourceLocations("classpath:/static/js/"); // /static/js/ 의 물리적인 위치
	}
}
