<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="dev.sample.auth.User" %>
<%
  User u = (User) session.getAttribute("LOGIN_USER");
  if (u == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
%>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8" />
  <title>우리 동네 소비 리포트</title>
  <style>
    body { font-family: sans-serif; margin: 24px; }
    .row { margin-bottom: 12px; }
    label { display: inline-block; width: 80px; }
    table { border-collapse: collapse; margin-top: 16px; width: 420px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; }
    th { background: #f3f3f3; }
    .meta { margin-top: 10px; color: #444; }
  </style>
</head>
<body>
  <h2>우리 동네 소비 리포트</h2>
  <p><strong><%= u.getName() %></strong> 님, 지역별 업종 소비 TOP을 조회합니다.</p>

  <div class="row">
    <label for="region">지역</label>
    <select id="region">
      <option value="서울">서울</option>
      <option value="경기">경기</option>
      <option value="인천">인천</option>
      <option value="강원">강원</option>
      <option value="충북">충북</option>
      <option value="충남">충남</option>
      <option value="대전">대전</option>
      <option value="세종">세종</option>
      <option value="전북">전북</option>
      <option value="전남">전남</option>
      <option value="광주">광주</option>
      <option value="경북">경북</option>
      <option value="경남">경남</option>
      <option value="대구">대구</option>
      <option value="울산">울산</option>
      <option value="부산">부산</option>
      <option value="제주">제주</option>
    </select>
  </div>

  <div class="row">
    <button type="button" onclick="loadReport()">중분류 업종 TOP5 조회</button>
  </div>

  <div class="meta" id="meta"></div>

  <table>
    <thead>
      <tr>
        <th>순위</th>
        <th>업종</th>
        <th>금액</th>
      </tr>
    </thead>
    <tbody id="rows"></tbody>
  </table>

  <form method="post" action="<%=request.getContextPath()%>/logout" style="margin-top:16px;">
    <button type="submit">로그아웃</button>
  </form>

  <script>
    async function loadReport() {
      const region = document.getElementById('region').value;
      const url = '<%=request.getContextPath()%>/report/region?region=' + encodeURIComponent(region);
      const res = await fetch(url, { method: 'GET' });
      const data = await res.json();

      const meta = document.getElementById('meta');
      const rows = document.getElementById('rows');
      rows.innerHTML = '';

      if (!data.success) {
        meta.textContent = '조회 실패: ' + (data.error || 'unknown error');
        return;
      }

      meta.textContent = '기준년월: ' + data.basYh + ' / 지역: ' + data.region;

      data.top.forEach((item, idx) => {
        const tr = document.createElement('tr');
        tr.innerHTML = '<td>' + (idx + 1) + '</td>' +
                       '<td>' + item.categoryName + '</td>' +
                       '<td>' + Number(item.amount).toLocaleString() + '</td>';
        rows.appendChild(tr);
      });
    }

    loadReport();
  </script>
</body>
</html>
