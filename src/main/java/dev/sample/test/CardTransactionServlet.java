package dev.sample.test;

import dev.sample.DBManager;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/service/card-data")
public class CardTransactionServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		// DBManager를 통해 GET 요청(Replica 3307)으로 자동 연결
		try (Connection con = DBManager.getConnection(req);
//				PreparedStatement ps = con.prepareStatement("SELECT * FROM CARD_TRANSACTION LIMIT 5");
				PreparedStatement ps = con.prepareStatement("SELECT * FROM test LIMIT 5");
				ResultSet rs = ps.executeQuery()) {

			out.println("=== The Huddle: 실시간 카드 트랜잭션 조회 (Replica) ===");
			while (rs.next()) {
				// getInt(1) 대신 안전하게 getString으로 데이터 확인
				out.println("데이터: " + rs.getString(1) + " | " + rs.getString(2));
			}
		} catch (Exception e) {
			resp.setStatus(500);
			out.println("서비스 오류: " + e.getMessage());
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		int randomId = (int) (Math.random() * 100) + 100;
		String sql = "INSERT INTO TEST (id, name, amount, description) VALUES (?, ?, ?, ?)";

		// DBManager를 통해 POST 요청(Source 3306)으로 자동 연결
		try (Connection con = DBManager.getConnection(req); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, randomId);
			ps.setString(2, "추가");
			ps.setInt(3, 10000);
			ps.setString(4, "테스트데이터 추가");

			int result = ps.executeUpdate();
			if (result > 0) {
				out.println("=== [Source DB] 데이터 삽입 성공 ===");
				out.println("생성된 랜덤 ID: " + randomId);
				out.println("DB 연결 대상: SOURCE (3306)");
			}

		} catch (Exception e) {
			resp.setStatus(500);
			out.println("데이터 삽입 오류: " + e.getMessage());
		}
	}

}
