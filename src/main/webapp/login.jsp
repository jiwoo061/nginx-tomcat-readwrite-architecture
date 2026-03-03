<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dev.sample.auth.User" %>
<%
  User u = (User) session.getAttribute("LOGIN_USER");
  if (u != null) {
    response.sendRedirect(request.getContextPath() + "/report");
    return;
  }
%>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Spending Insight - 로그인</title>
  <style>
    :root {
      --bg: #f4f6fa;
      --card: #ffffff;
      --text: #0f172a;
      --muted: #64748b;
      --line: #dbe3ef;
      --blue: #2f6fc0;
      --blue-strong: #235aa0;
      --shadow: 0 10px 24px rgba(25, 67, 127, 0.12);
      --radius: 18px;
    }

    * { box-sizing: border-box; }

    body {
      margin: 0;
      min-height: 100vh;
      font-family: "Pretendard", "Noto Sans KR", "Apple SD Gothic Neo", sans-serif;
      background: var(--bg);
      color: var(--text);
      display: grid;
      place-items: center;
      padding: 24px;
    }

    .wrap {
      width: min(100%, 560px);
      text-align: center;
    }

    .logo {
      width: 96px;
      height: 96px;
      margin: 0 auto 20px;
      border-radius: 24px;
      background: var(--blue);
      display: grid;
      place-items: center;
      color: #fff;
      font-size: 46px;
      line-height: 1;
    }

    h1 {
      margin: 0;
      font-size: clamp(34px, 4vw, 48px);
      font-weight: 800;
      letter-spacing: -0.03em;
    }

    .subtitle {
      margin: 10px 0 40px;
      font-size: 20px;
      color: var(--muted);
      letter-spacing: -0.01em;
    }

    form {
      text-align: left;
      background: var(--card);
      border: 1px solid var(--line);
      border-radius: var(--radius);
      padding: 28px;
      box-shadow: var(--shadow);
    }

    label {
      display: block;
      font-size: 20px;
      font-weight: 700;
      margin: 0 0 12px;
    }

    .field {
      margin-bottom: 18px;
      position: relative;
    }

    input {
      width: 100%;
      border: 1px solid #cfd9e8;
      border-radius: 16px;
      height: 68px;
      padding: 0 22px;
      font-size: 34px;
      letter-spacing: -0.02em;
      outline: none;
      color: #334155;
      background: #fff;
    }

    input::placeholder {
      color: #8aa0be;
    }

    input:focus {
      border-color: #8fb2e2;
      box-shadow: 0 0 0 4px rgba(47, 111, 192, 0.12);
    }

    .password-wrap {
      position: relative;
    }

    .toggle-btn {
      position: absolute;
      right: 14px;
      top: 50%;
      transform: translateY(-50%);
      border: 0;
      background: transparent;
      color: #8092aa;
      font-size: 24px;
      cursor: pointer;
    }

    .submit-btn {
      width: 100%;
      height: 72px;
      border: 0;
      border-radius: 18px;
      margin-top: 12px;
      background: var(--blue);
      color: #fff;
      font-size: 34px;
      font-weight: 800;
      letter-spacing: -0.02em;
      cursor: pointer;
      box-shadow: 0 12px 24px rgba(47, 111, 192, 0.24);
      transition: background .2s ease;
    }

    .submit-btn:hover {
      background: var(--blue-strong);
    }

    .footer {
      margin-top: 22px;
      color: #6480a3;
      font-size: 16px;
    }

    @media (max-width: 640px) {
      h1 { font-size: 42px; }
      .subtitle { font-size: 18px; margin-bottom: 28px; }
      label { font-size: 17px; }
      input { height: 54px; font-size: 19px; }
      .submit-btn { height: 58px; font-size: 21px; }
      form { padding: 20px; }
    }
  </style>
</head>
<body>
  <main class="wrap">
    <div class="logo">📍</div>
    <h1>Spending Insight</h1>
    <p class="subtitle">지역별 소비 패턴 분석</p>

    <form method="post" action="<%=request.getContextPath()%>/auth/login">
      <div class="field">
        <label for="loginId">아이디</label>
        <input id="loginId" name="loginId" autocomplete="username" placeholder="아이디를 입력하세요" required />
      </div>

      <div class="field">
        <label for="password">비밀번호</label>
        <div class="password-wrap">
          <input id="password" name="password" type="password" autocomplete="current-password" placeholder="비밀번호를 입력하세요" required />
          <button class="toggle-btn" type="button" onclick="togglePassword()" aria-label="비밀번호 표시 토글">👁</button>
        </div>
      </div>

      <button class="submit-btn" type="submit">로그인</button>
    </form>

    <p class="footer">© 2026 Spending Insight Map</p>
  </main>

  <script>
    function togglePassword() {
      const pw = document.getElementById('password');
      pw.type = pw.type === 'password' ? 'text' : 'password';
    }
  </script>
</body>
</html>
