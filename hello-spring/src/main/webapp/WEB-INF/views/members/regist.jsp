<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
  <jsp:include page="/WEB-INF/views/templates/header.jsp">
    <jsp:param value="회원 가입" name="title" />
    <jsp:param value="<script type='text/javascript' src='/js/members.js'></script>" 
               name="scripts" />
  </jsp:include>
    <h1>회원 가입</h1>
    <%-- action ==> form 내부의 value를 전송할 엔드포인트 --%>
    <form:form modelAttribute="registVO" method="post" action="/regist">
      <div class="grid regist">
        <label for="email">이메일</label>
        <div class="input-div">
          <input
            type="email"
            id="email"
            name="email"
            placeholder="이메일을 입력하세요."
            value="${inputData.email}${errorData.email}"
          />
          <c:if test="${not empty errorMessage}">
            <div class="validation-error">${errorMessage}</div>
          </c:if>
          <form:errors path="email" cssClass="validation-error" element="div" />
        </div>
        <label for="name">이름</label>
        <div class="input-div">
          <input
            type="text"
            id="name"
            name="name"
            placeholder="이름을 입력하세요."
            value="${inputData.name}${errorData.name}"
          />
          <form:errors path="name" cssClass="validation-error" element="div" />
        </div>

        <label for="password">비밀번호</label>
        <div class="input-div">
          <input
            type="password"
            id="password"
            name="password"
            placeholder="비밀번호를 입력하세요."
          />
          <form:errors
            path="password"
            cssClass="validation-error"
            element="div"
          />
        </div>
        <%-- 비밀번호 두 번 입력하기 ==> 두 비밀번호가 일치할 때만 회원가입 시키기. --%>
        <label for="confirm-password">비밀번호 확인</label>
        <div class="input-div">
          <input
            type="password"
            id="confirm-password"
            name="confirm-password"
          />
        </div>

        <%-- 비밀번호 한 번 입력하기 ==> 비밀번호를 확인하는 기능 --%>
        <label for="show-password">비밀번호 확인하기</label>
        <input type="checkbox" id="show-password" />

        <div class="btn-group">
          <div class="right-align">
            <input type="submit" value="등록" />
          </div>
        </div>
      </div>
    </form:form>
  <jsp:include page="/WEB-INF/views/templates/footer.jsp"></jsp:include>
