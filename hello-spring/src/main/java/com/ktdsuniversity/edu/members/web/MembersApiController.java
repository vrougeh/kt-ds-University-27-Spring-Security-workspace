package com.ktdsuniversity.edu.members.web;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ktdsuniversity.edu.common.utils.AuthUtils;
import com.ktdsuniversity.edu.common.utils.ServletUtils;
import com.ktdsuniversity.edu.exceptions.HelloSpringApiException;
import com.ktdsuniversity.edu.members.service.MembersService;
import com.ktdsuniversity.edu.members.vo.MembersVO;
import com.ktdsuniversity.edu.members.vo.request.MemberSearchVO;
import com.ktdsuniversity.edu.members.vo.request.RegistVO;
import com.ktdsuniversity.edu.members.vo.request.UpdateVO;
import com.ktdsuniversity.edu.members.vo.response.DuplicateResultVO;
import com.ktdsuniversity.edu.members.vo.response.SearchResultVO;

import jakarta.validation.Valid;

public class MembersApiController {
private static final Logger logger = LoggerFactory.getLogger(MembersApiController.class);
	
	@Autowired
	private MembersService membersService;
	
	@ResponseBody
	@GetMapping("/api/regist/check/duplicate/{email}")
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
	
	@PreAuthorize("isAnonymous()")
	@PostMapping("/api/regist")
	@ResponseBody
	public Map<String, Boolean> doRegistAction(
			@Valid @ModelAttribute RegistVO registVO,
			BindingResult bindingResult,
			Model model) {
		
		if (bindingResult.hasErrors()) {
			throw new HelloSpringApiException("잘못된 입력값", HttpStatus.BAD_REQUEST.value(), bindingResult.getFieldErrors());
		}
		boolean createResult = this.membersService.createNewMember(registVO);
		logger.debug("회원 가입 결과? {}", createResult);
		return Map.of("result", createResult);
	}
	
	@PreAuthorize("isAuthenticated() and #email == authentication.principal.email") // 메소드의 파라미터로 전달된 값(email)과 authentication에 할당된 email값을 비교한다.
	@GetMapping("/api/member/{email}")
	@ResponseBody
	public MembersVO viewMemberPage(@PathVariable String email, 
			Model model) {
		MembersVO searchReuslt = this.membersService.findMemberByEmail(email);
		return searchReuslt;
	}
	
	// 본인의 정보만 조회 가능하도록 개선.
	// 다른 사람의 정보를 조회하려 할 경우 예외 발생 ==> 잘못된 접근입니다.
	@PreAuthorize("isAuthenticated() and #email == authentication.principal.email")
	@PutMapping("/api/member/update/{email}")
	@ResponseBody
	public Map<String, Boolean> doUpdateAction(@PathVariable String email,
			UpdateVO updateVO) {
		updateVO.setEmail(email);
		boolean updateResult = this.membersService.updateMemberByEmail(updateVO);
		logger.debug("수정 결과? {}", updateResult);
		return Map.of("result", updateResult);
	}
	
	// 본인의 정보만 조회 가능하도록 개선.
	// 다른 사람의 정보를 조회하려 할 경우 예외 발생 ==> 잘못된 접근입니다.
	@PreAuthorize("isAuthenticated() and #id == authentication.principal.email")
	@DeleteMapping("/api/member/delete")
	@ResponseBody
	public Map<String, Boolean> doDeleteAction(@RequestParam String id) {
		boolean updateResult = this.membersService.deleteMemberByEmail(id);
		logger.debug("삭제 결과? {}", updateResult);
		return Map.of("result", updateResult);
	}
	
	@PreAuthorize("hasRole('RL-20260414-000002')")
	@GetMapping("/api/members")
	@ResponseBody
	public Map<String, Object> viewMembersPage(Model model, MemberSearchVO membersSearchVO) {
		SearchResultVO searchResult = this.membersService.findMembersList(membersSearchVO);
		model.addAttribute("searchList", searchResult.getResult());
		model.addAttribute("searchCount", searchResult.getCount());
		model.addAttribute("pagination", membersSearchVO);
		return Map.of("result", searchResult, "pagination", membersSearchVO);
	}
	
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/delete-me")
	@ResponseBody
	public Map<String, Object> doDeleteAction(Authentication authentication) {
		// 1. 로그인 세션에서 회원의 이메일을 가져온다.
		MembersVO loginUser = AuthUtils.getPrincipal();;
		String email = loginUser.getEmail();
		
		// 2. MEMBERS 테이블에서 회원의 정보를 이메일을 이용해 삭제한다.
		boolean deleteSuccess = this.membersService.deleteMemberByEmail(email);
		logger.debug("탈퇴 성공? {}", deleteSuccess);
		
		// 3. 현재 로그인된 사용자를 로그아웃 시킨다.
		LogoutHandler logoutHandler = new SecurityContextLogoutHandler();
		logoutHandler.logout(
				ServletUtils.getRequest(), 
				ServletUtils.getResponse(), 
				authentication);
		
		// 4. "members/deletesuccess" 페이지를 보여준다.
		//    "탈퇴가 완료됐습니다. 다음에 다시 만나요!"
		return Map.of("result", deleteSuccess);
	}

}
