<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>게시글 작성</title>
    <script type="text/javascript" src="/js/jquery-4.0.0.slim.min.js"></script>
    <script type="text/javascript" src="/js/board.js"></script>
    <link rel="stylesheet" type="text/css" href="/css/hello-spring.css" />
  </head>
  <body>
    <h1>게시글 작성</h1>
    <%-- action ==> form 내부의 value를 전송할 엔드포인트 --%>
    <%-- form:form modelAttibute ==> 
            form 태그 내부의 input, textarea, select 등을 
            컨트롤러 보내기 위한 아이디
            보편적으로 변수의 이름(엔드포인트의) --%>
    <form:form
      modelAttribute="writeVO"
      method="post"
      action="/write"
      enctype="multipart/form-data"
    >
      <div class="grid write">
        <label for="subject">제목</label>
        <div class="input-div">
          <input
            type="text"
            id="subject"
            name="subject"
            placeholder="제목을 입력하세요."
            value="${inputData.subject}"
          />
          <form:errors path="subject" 
                       cssClass="validation-error" 
                       element="div" />
        </div>

        <label for="attach-files">첨부파일</label>
        <div id="attach-files" class="attach-files">
          <input type="file" name="attachFile" />
          <button type="button" class="add-file">+</button>
        </div>

        <label for="content">내용</label>
        <textarea id="content" name="content" placeholder="내용을 입력하세요">
${inputData.content}</textarea
        >

        <div class="btn-group">
          <div class="right-align">
            <input type="submit" value="저장" />
          </div>
        </div>
      </div>
    </form:form>
  </body>
</html>
