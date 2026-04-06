<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>로그인</title>
    <link rel="stylesheet" type="text/css" href="/css/hello-spring.css" />

    <script type="text/javascript" src="/js/jquery-4.0.0.slim.min.js"></script>
    <script type="text/javascript" src="/js/members.js"></script>
  </head>
  <body>
    <h1>로그인</h1>
    <form:form modelAttribute="loginVO" method="post" action="/login">
      <div class="grid login">
        <label for="login-email">이메일</label>
        <div class="input-div">
          <input
            type="email"
            id="login-email"
            name="email"
            placeholder="이메일을 입력하세요."
            value="${inputData.email}"
          />
          <form:errors path="email" cssClass="validation-error" element="div" />
        </div>

        <label for="login-password">비밀번호</label>
        <div class="input-div">
          <input
            type="password"
            id="login-password"
            name="password"
            placeholder="비밀번호를 입력하세요."
          />
          <form:errors
            path="password"
            cssClass="validation-error"
            element="div"
          />
        </div>

        <div class="btn-group">
          <div class="right-align">
            <input type="submit" value="로그인" />
          </div>
        </div>
      </div>
    </form:form>
  </body>
</html>
