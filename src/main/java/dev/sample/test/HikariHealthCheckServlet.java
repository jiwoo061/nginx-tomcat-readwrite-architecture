package dev.sample.test;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/test/hikari")
public class HikariHealthCheckServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		resp.setContentType("text/plain; charset=UTF-8");
		PrintWriter out = resp.getWriter();

		ServletContext ctx = getServletContext();
		Object sourceObj = ctx.getAttribute("SOURCE_DS");
		Object replicaObj = ctx.getAttribute("REPLICA_DS");
		if (sourceObj == null || replicaObj == null) {
			resp.setStatus(500);
			out.println("FAIL: DATA_SOURCE not found in ServletContext");
			return;
		}

		DataSource sourceDs = (DataSource) sourceObj;
		DataSource replicaDs = (DataSource) replicaObj;

		String sql = "SELECT * FROM CARD_TRANSACTION LIMIT 3";

		long start = System.currentTimeMillis();
		// 1. Source DB 체크
		try (Connection con = sourceDs.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			if (rs.next()) {
		        // 첫 번째 컬럼이 문자열일 수 있으므로 getString으로 받거나, 
		        // 성공했다는 사실 자체만 출력하는 것이 안전합니다.
		        String firstValue = rs.getString(1); 

		        long elapsed = System.currentTimeMillis() - start;
		        out.println("Source Status: OK");
		        out.println("queryResult (first col)=" + firstValue);
		        out.println("elapsedMs=" + elapsed);
		    }
		} catch (Exception e) {
			resp.setStatus(500);
			out.println("FAIL: " + e.getClass().getName() + " - " + e.getMessage());
		}

		out.println("---------------------------------------");

		// 2. Replica DB 체크
		try (Connection con = replicaDs.getConnection();
				PreparedStatement ps = con.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {
//			rs.next();
//			out.println("REPLICA Status: OK, Result=" + rs.getInt(1));
			if (rs.next()) {
		        out.println("REPLICA Status: OK, First Row Data=" + rs.getString(1));
		    }
		} catch (Exception e) {
			out.println("REPLICA FAIL: " + e.getMessage());
		}
	}
}
