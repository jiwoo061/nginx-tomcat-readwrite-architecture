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
<html lang="ko">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Spending Insight - 리포트</title>
  <style>
    :root {
      --bg: #eef2f8;
      --card: #ffffff;
      --text: #0f172a;
      --muted: #64748b;
      --line: #d6deea;
      --blue: #2f6fc0;
      --soft-blue: #dce9fb;
      --chip: #edf1f6;
      --shadow: 0 8px 20px rgba(15, 23, 42, .05);
      --radius: 20px;
    }

    * { box-sizing: border-box; }

    body {
      margin: 0;
      font-family: "Pretendard", "Noto Sans KR", "Apple SD Gothic Neo", sans-serif;
      background: var(--bg);
      color: var(--text);
    }

    .topbar {
      background: #fff;
      border-bottom: 1px solid var(--line);
      padding: 18px 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 12px;
      position: sticky;
      top: 0;
      z-index: 20;
    }

    .title {
      margin: 0;
      font-size: 34px;
      font-weight: 800;
      letter-spacing: -0.03em;
    }

    .desc {
      margin: 6px 0 0;
      color: var(--muted);
      font-size: 16px;
    }

    .actions {
      display: flex;
      gap: 10px;
    }

    .icon-btn {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      border: 0;
      background: #edf2f8;
      color: #5e789d;
      font-size: 24px;
      cursor: pointer;
    }

    .container {
      width: min(1060px, 100% - 40px);
      margin: 26px auto;
      display: grid;
      gap: 18px;
    }

    .card {
      background: var(--card);
      border: 1px solid var(--line);
      border-radius: var(--radius);
      padding: 24px;
      box-shadow: var(--shadow);
    }

    .card h2 {
      margin: 0 0 6px;
      font-size: 38px;
      letter-spacing: -0.02em;
    }

    .card p {
      margin: 0 0 20px;
      color: var(--muted);
      font-size: 15px;
    }

    .region-grid {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: 10px;
    }

    .region-btn {
      border: 0;
      border-radius: 14px;
      height: 66px;
      font-size: 34px;
      font-weight: 700;
      background: var(--chip);
      color: #243b5a;
      cursor: pointer;
      transition: all .15s ease;
    }

    .region-btn:hover {
      filter: brightness(0.98);
    }

    .region-btn.active {
      background: var(--blue);
      color: #fff;
      box-shadow: inset 0 -2px 0 rgba(0,0,0,.12);
    }

    .report-head {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 10px;
      margin-bottom: 10px;
      flex-wrap: wrap;
    }

    .report-head h3 {
      margin: 0;
      font-size: 38px;
      letter-spacing: -0.02em;
    }

    .meta {
      margin: 0;
      color: var(--muted);
      font-size: 16px;
    }

    .list {
      display: grid;
      gap: 10px;
      margin-top: 12px;
    }

    .row {
      display: grid;
      grid-template-columns: 50px 90px 1fr minmax(160px, 2fr) 70px;
      align-items: center;
      gap: 10px;
      padding: 6px 4px;
    }

    .rank {
      width: 44px;
      height: 44px;
      border-radius: 50%;
      background: var(--soft-blue);
      color: #1e5eaf;
      display: grid;
      place-items: center;
      font-size: 27px;
      font-weight: 800;
    }

    .badge {
      width: 60px;
      height: 52px;
      border-radius: 16px;
      display: grid;
      place-items: center;
      background: #edf1f6;
      color: #7389a5;
      font-size: 25px;
    }

    .name {
      font-size: 31px;
      font-weight: 700;
      letter-spacing: -0.02em;
    }

    .bar-wrap {
      height: 12px;
      background: #e5ebf3;
      border-radius: 999px;
      overflow: hidden;
    }

    .bar {
      height: 100%;
      border-radius: 999px;
      background: var(--blue);
      min-width: 8px;
    }

    .pct {
      text-align: right;
      color: #5f7695;
      font-size: 30px;
      font-weight: 700;
    }

    .error {
      color: #b91c1c;
      font-weight: 600;
    }

    @media (max-width: 980px) {
      .title { font-size: 28px; }
      .card h2, .report-head h3 { font-size: 30px; }
      .region-btn { font-size: 25px; height: 56px; }
      .row { grid-template-columns: 44px 62px 1fr 1.4fr 60px; }
      .rank { width: 40px; height: 40px; font-size: 22px; }
      .badge { width: 52px; height: 44px; font-size: 22px; }
      .name { font-size: 22px; }
      .pct { font-size: 24px; }
    }

    @media (max-width: 680px) {
      .container { width: min(1060px, 100% - 20px); }
      .region-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
      .row { grid-template-columns: 36px 46px 1fr; grid-template-areas: "rank badge name" "bar bar pct"; }
      .rank { grid-area: rank; width: 34px; height: 34px; font-size: 17px; }
      .badge { grid-area: badge; width: 42px; height: 36px; font-size: 16px; border-radius: 12px; }
      .name { grid-area: name; font-size: 18px; }
      .bar-wrap { grid-area: bar; margin-left: 6px; margin-top: 8px; }
      .pct { grid-area: pct; font-size: 18px; margin-top: 4px; }
    }
  </style>
</head>
<body>
  <header class="topbar">
    <div>
      <h1 class="title"><%=u.getName()%>님, 반갑습니다! 👋</h1>
      <p class="desc">오늘의 소비 인사이트를 확인하세요</p>
    </div>
    <div class="actions">
      <button class="icon-btn" type="button" title="프로필">👤</button>
      <form method="post" action="<%=request.getContextPath()%>/logout" style="margin:0;">
        <button class="icon-btn" type="submit" title="로그아웃">↪</button>
      </form>
    </div>
  </header>

  <main class="container">
    <section class="card">
      <h2>📍 지역을 선택하세요</h2>
      <p>지역을 클릭하면 소비 순위를 확인할 수 있습니다</p>
      <div class="region-grid" id="regionGrid"></div>
    </section>

    <section class="card">
      <div class="report-head">
        <h3 id="reportTitle">- 소비 업종 순위</h3>
        <p class="meta" id="meta">로딩 중...</p>
      </div>
      <div class="list" id="rows"></div>
    </section>
  </main>

  <script>
    const REGIONS = ["강원", "경기", "경남", "경북", "광주", "대구", "대전", "부산", "서울", "세종", "울산", "인천", "전남", "전북", "제주", "충남", "충북"];
    let selectedRegion = '서울';

    function iconByRank(rank) {
      if (rank === 1) return '🛍';
      if (rank === 2) return '🍽';
      if (rank === 3) return '🎓';
      if (rank === 4) return '🚗';
      return '🎨';
    }

    function renderRegionButtons() {
      const grid = document.getElementById('regionGrid');
      grid.innerHTML = '';
      REGIONS.forEach(region => {
        const btn = document.createElement('button');
        btn.type = 'button';
        btn.className = 'region-btn' + (region === selectedRegion ? ' active' : '');
        btn.textContent = region;
        btn.onclick = () => {
          selectedRegion = region;
          renderRegionButtons();
          loadReport();
        };
        grid.appendChild(btn);
      });
    }

    function renderRows(items) {
      const rows = document.getElementById('rows');
      rows.innerHTML = '';

      const total = items.reduce((acc, item) => acc + Number(item.amount || 0), 0);
      const maxPct = Math.max(...items.map(i => total > 0 ? (Number(i.amount || 0) / total) * 100 : 0), 1);

      items.forEach((item, idx) => {
        const rank = idx + 1;
        const pct = total > 0 ? (Number(item.amount || 0) / total) * 100 : 0;
        const bar = (pct / maxPct) * 100;

        const row = document.createElement('div');
        row.className = 'row';
        row.innerHTML = '' +
          '<div class="rank">' + rank + '</div>' +
          '<div class="badge">' + iconByRank(rank) + '</div>' +
          '<div class="name">' + item.categoryName + '</div>' +
          '<div class="bar-wrap"><div class="bar" style="width:' + bar.toFixed(1) + '%"></div></div>' +
          '<div class="pct">' + pct.toFixed(1) + '%</div>';

        rows.appendChild(row);
      });
    }

    async function loadReport() {
      const meta = document.getElementById('meta');
      const title = document.getElementById('reportTitle');
      title.textContent = selectedRegion + ' 소비 업종 순위';
      meta.textContent = '로딩 중...';

      try {
        const url = '<%=request.getContextPath()%>/report/region?region=' + encodeURIComponent(selectedRegion);
        const res = await fetch(url, { method: 'GET' });
        const data = await res.json();

        if (!res.ok || !data.success) {
          document.getElementById('rows').innerHTML = '';
          meta.innerHTML = '<span class="error">조회 실패: ' + (data.error || 'unknown error') + '</span>';
          return;
        }

        meta.textContent = '기준년월: ' + data.basYh + ' / 지역: ' + data.region;
        renderRows(data.top || []);
      } catch (e) {
        document.getElementById('rows').innerHTML = '';
        meta.innerHTML = '<span class="error">네트워크 오류로 조회에 실패했습니다.</span>';
      }
    }

    renderRegionButtons();
    loadReport();
  </script>
</body>
</html>
