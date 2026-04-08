package com.ktdsuniversity.edu.members.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.members.service.MembersService;
import com.ktdsuniversity.edu.members.vo.MembersVO;
import com.ktdsuniversity.edu.members.vo.request.LoginVO;
import com.ktdsuniversity.edu.members.vo.request.RegistVO;
import com.ktdsuniversity.edu.members.vo.request.UpdateVO;
import com.ktdsuniversity.edu.members.vo.response.DuplicateResultVO;
import com.ktdsuniversity.edu.members.vo.response.SearchResultVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * EndPoint 생성/관리.
 * + Validation Check
 */

@Controller
public class MembersController {

	private static final Logger logger = LoggerFactory.getLogger(MembersController.class);
	
	@Autowired
	private MembersService membersService;
	
	@ResponseBody
	@GetMapping("/regist/check/duplicate/{email}")
	public DuplicateResultVO doCheckDuplicateEmailAction(@PathVariable String email) {
		
		// email이 이미 사용중인지 확인한다.
		MembersVO membersVO = this.membersService.findMemberByEmail(email);
		
		// 확인된 결과를 브라우저에게 JSON으로 전송한다.
		// 이미 사용중 ==> {email: "test@gmail", duplicate: true}
		// 사용중이지 않음 ==> {email: "test@gmail", duplicate: false}
		DuplicateResultVO result = new DuplicateResultVO();
		result.setEmail(email);
		result.setDuplicate( membersVO != null );
		return result;
	}
	
	
	@GetMapping("/regist")
	public String viewRegistPage() {
		return "members/regist";
	}
	
	@PostMapping("/regist")
	public String doRegistAction(
			@Valid @ModelAttribute RegistVO registVO,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("inputData", registVO);
			return "members/regist";
		}
		boolean createResult = this.membersService.createNewMember(registVO);
		logger.debug("회원 가입 결과? {}", createResult);
		return "redirect:/login";
	}
	
	/*
	 * /member/view/사용자아이디 ==> 회원 정보 조회 하기.
	 * /member/update/사용자아이디 ==> 회원 정보 수정 페이지 보기.
	 * /member/update/사용자아이디 ==> 회원 정보 수정 하기.
	 * /member/delete?id=사용자아이디 ==> 회원 정보 삭제 하기.
	 */
	@GetMapping("/member/view/{email}")
	public String viewMemberPage(@PathVariable String email, 
			Model model) {
		MembersVO searchReuslt = this.membersService.findMemberByEmail(email);
		model.addAttribute("member", searchReuslt);
		return "members/view";
	}
	
	@GetMapping("/member/update/{email}")
	public String viewUpdatePage(@PathVariable String email,
			Model model) {
		MembersVO searchReuslt = this.membersService.findMemberByEmail(email);
		model.addAttribute("member", searchReuslt);
		return "members/update";
	}
	
	@PostMapping("/member/update/{email}")
	public String doUpdateAction(@PathVariable String email,
			UpdateVO updateVO) {
		updateVO.setEmail(email);
		boolean updateResult = this.membersService.updateMemberByEmail(updateVO);
		logger.debug("수정 결과? {}", updateResult);
		return "redirect:/member/view/" + email;
	}
	
	@GetMapping("/member/delete")
	public String doDeleteAction(@RequestParam String id) {
		boolean updateResult = this.membersService.deleteMemberByEmail(id);
		logger.debug("삭제 결과? {}", updateResult);
		return "redirect:/member";
	}
	
	// /member ==> 회원들의 목록이 조회되도록 코드를 작성.
	//     ==> 회원 목록 조회.
	//     ==> members/list.jsp : 회원 목록 반복.
	//                          : 회원의 수 출력
	//                          : 회원의 수가 없을 때, "등록된 회원이 없습니다" 출력
	//                          : 목록 아래에는 "새로운 회원 등록" 링크 추가.
	@GetMapping("/member")
	public String viewMembersPage(Model model) {
		SearchResultVO searchResult = this.membersService.findMembersList();
		model.addAttribute("searchList", searchResult.getResult());
		model.addAttribute("searchCount", searchResult.getCount());
		return "members/newlist";
	}
	
	@GetMapping("/login")
	public String viewLoginPage() {
		return "members/login"; 
	}
	
	@PostMapping("/login")
	public String doLoginAction(
			@Valid @ModelAttribute LoginVO loginVO,
			BindingResult bindingResult,
			Model model,
			@RequestParam(required = false, defaultValue = "/") String go,
			HttpServletRequest request) {
		
		if (bindingResult.hasErrors()) {
			model.addAttribute("loginData", loginVO);
			return "members/login";
		}
		
		String userIp = request.getRemoteAddr();
		loginVO.setIp(userIp);
		
		MembersVO member = 
				this.membersService
				    .findMemberByEmailAndPassword(loginVO);
		
		// 서버의 세션을 삭제한다.
		// 로그아웃.
		request.getSession().invalidate();
		
		// request.getSession(); <== HttpRequestHeader로 전달된 JSESSIONID의 객체를 반환.
		// request.getSession(true); <== 기존 JESSIONID로 발급된 세션객체는 버리고, 새로운 ID의 세션객체를 생성 후 반환.
		HttpSession session = request.getSession(true);
		session.setAttribute("__LOGIN_DATA__", member);
		
		return "redirect:" + go;
	}
	
	@GetMapping("/logout")
	public String doLogoutAction(HttpSession session) {
		session.invalidate();
		return "redirect:/login";
	}
	
	@GetMapping("/delete-me")
	public String doDeleteAction(
			@SessionAttribute("__LOGIN_DATA__") MembersVO loginMember,
			HttpSession session) {
		// 1. 로그인 세션에서 회원의 이메일을 가져온다.
		String email = loginMember.getEmail();
		
		// 2. MEMBERS 테이블에서 회원의 정보를 이메일을 이용해 삭제한다.
		boolean deleteSuccess = this.membersService.deleteMemberByEmail(email);
		logger.debug("탈퇴 성공? {}", deleteSuccess);
		
		// 3. 현재 로그인된 사용자를 로그아웃 시킨다.
		session.invalidate();
		
		// 4. "members/deletesuccess" 페이지를 보여준다.
		//    "탈퇴가 완료됐습니다. 다음에 다시 만나요!"
		return "members/deletesuccess";
	}
}






