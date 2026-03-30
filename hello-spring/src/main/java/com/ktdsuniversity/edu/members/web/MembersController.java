package com.ktdsuniversity.edu.members.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ktdsuniversity.edu.members.service.MembersService;
import com.ktdsuniversity.edu.members.vo.MembersVO;
import com.ktdsuniversity.edu.members.vo.request.RegistVO;
import com.ktdsuniversity.edu.members.vo.request.UpdateVO;
import com.ktdsuniversity.edu.members.vo.response.SearchResultVO;

@Controller
public class MembersController {

	@Autowired
	private MembersService membersService;
	
	@GetMapping("/regist")
	public String viewRegistPage() {
		return "members/regist";
	}
	
	@PostMapping("/regist")
	public String doRegistAction(RegistVO registVO) {
		boolean createResult = this.membersService.createNewMember(registVO);
		System.out.println("회원 가입 결과? " + createResult);
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
		System.out.println("수정 결과? " + updateResult);
		return "redirect:/member/view/" + email;
	}
	
	@GetMapping("/member/delete")
	public String doDeleteAction(@RequestParam String id) {
		boolean updateResult = this.membersService.deleteMemberByEmail(id);
		System.out.println("삭제 결과? " + updateResult);
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
}






