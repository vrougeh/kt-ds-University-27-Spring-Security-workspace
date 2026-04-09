package com.ktdsuniversity.edu.replies.service;

import com.ktdsuniversity.edu.replies.vo.RepliesVO;
import com.ktdsuniversity.edu.replies.vo.request.CreateVO;
import com.ktdsuniversity.edu.replies.vo.response.SearchResultVO;

public interface RepliesService {

	RepliesVO createNewReply(CreateVO createVO);

	SearchResultVO findRepliesByArticleId(String articleId);

}
