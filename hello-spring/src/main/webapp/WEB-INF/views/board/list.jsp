<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>게시글 목록</title>
  </head>
  <body>
    <h1>게시글 목록</h1>
    <div>총 ${searchCount}개의 게시글이 검색되었습니다.</div>
    <!-- list.get(0).getId() -->
    <div>아이디: ${searchResult[0].id}</div>
    <div>제목: ${searchResult[0].subject}</div>
    <div>내용: ${searchResult[0].content}</div>
    <div>이메일: ${searchResult[0].email}</div>
    <div>조회수: ${searchResult[0].viewCnt}</div>
    <div>작성날짜 및 시간: ${searchResult[0].crtDt}</div>
    <div>수정날짜 및 시간: ${searchResult[0].mdfyDt}</div>
    <div>첨부파일 이름: ${searchResult[0].fileName}</div>
    <div>첨부파일 이름: ${searchResult[0].originFileName}</div>
  </body>
</html>
