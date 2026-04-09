package com.ktdsuniversity.edu.replies.web;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.ktdsuniversity.edu.exceptions.HelloSpringApiException;
import com.ktdsuniversity.edu.members.vo.MembersVO;
import com.ktdsuniversity.edu.replies.service.RepliesService;
import com.ktdsuniversity.edu.replies.vo.RepliesVO;
import com.ktdsuniversity.edu.replies.vo.request.CreateVO;
import com.ktdsuniversity.edu.replies.vo.response.SearchResultVO;

import jakarta.validation.Valid;

@Controller
public class RepliesController {

	private static final Logger logger = LoggerFactory.getLogger(RepliesController.class);
	
	@Autowired
	private RepliesService repliesService;
	
	@ResponseBody
	@GetMapping("/api/replies/{articleId}")
	public SearchResultVO getRepliesList(@PathVariable String articleId) {
		SearchResultVO searchResult = this.repliesService.findRepliesByArticleId(articleId);
		return searchResult;
	}
	
	@ResponseBody
	@PostMapping("/api/replies-with-file")
	public RepliesVO doCreateNewReplyWithFileAction(
			@Valid CreateVO createVO,
			BindingResult bindingResult,
			@SessionAttribute("__LOGIN_DATA__") MembersVO loginMember) {
		
		if (bindingResult.hasErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			throw new HelloSpringApiException("파라미터가 충분하지 않습니다.", HttpStatus.BAD_REQUEST.value(), errors);
		}
		
		createVO.setEmail(loginMember.getEmail());
		
		logger.debug("reply: {}", createVO.getReply());
		logger.debug("email: {}", createVO.getEmail());
		logger.debug("articleId: {}", createVO.getArticleId());
		logger.debug("parentReplyId: {}", createVO.getParentReplyId());
		
		RepliesVO createResult = this.repliesService.createNewReply(createVO);
		
		return createResult;
		
	}
	
	
	// AJAX(API) 요청 / 반환.
	// 요청 데이터 + 반환 데이터 ==> JSON
	@ResponseBody
	@PostMapping("/api/replies")
	public RepliesVO doCreateNewReplyAction(
			@RequestBody @Valid CreateVO createVO,
			BindingResult bindingResult,
			@SessionAttribute("__LOGIN_DATA__") MembersVO loginMember) {
		
		if (bindingResult.hasErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			throw new HelloSpringApiException("파라미터가 충분하지 않습니다.", HttpStatus.BAD_REQUEST.value(), errors);
		}
		
		createVO.setEmail(loginMember.getEmail());
		
		logger.debug("reply: {}", createVO.getReply());
		logger.debug("email: {}", createVO.getEmail());
		logger.debug("articleId: {}", createVO.getArticleId());
		logger.debug("parentReplyId: {}", createVO.getParentReplyId());
		
		RepliesVO createResult = this.repliesService.createNewReply(createVO);
		
		return createResult;
	}
	
}
