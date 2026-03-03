<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dev.sample.auth.User" %>
<html>
<body>
<%
  User u = (User) session.getAttribute("LOGIN_USER");
  if (u != null) {
%>
    <h2><%=u.getName()%> 님 로그인 상태입니다</h2>

    <form method="post" action="<%=request.getContextPath()%>/logout">
      <button type="submit">로그아웃</button>
    </form>

    <p><a href="<%=request.getContextPath()%>/report">우리 동네 소비 리포트 보기</a></p>

<%
    return; // 아래 로그인 폼은 출력 안 함
  }
%>

  <h2>Login</h2>
  <form method="post" action="<%=request.getContextPath()%>/auth/login">
    <input name="loginId" placeholder="loginId" />
    <input name="password" type="password" placeholder="password" />
    <button type="submit">로그인</button>
  </form>

</body>
</html>
