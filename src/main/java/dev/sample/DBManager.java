package dev.sample;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

public class DBManager {
	public static Connection getConnection(HttpServletRequest req) throws SQLException {
        ServletContext ctx = req.getServletContext();
        DataSource ds;
        String dbName;
        
        // Read 요청
        // 요청 메서드가 GET이면 읽기 전용(Replica) 사용
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            ds = (DataSource) ctx.getAttribute("REPLICA_DS");
            dbName = "REPLICA (3307)"; // 로그용 변수
        } 
        // CUD 요청
        // POST, PUT, DELETE 등은 마스터(Source) 사용
        else {
            ds = (DataSource) ctx.getAttribute("SOURCE_DS");
            dbName = "SOURCE (3306)"; // 로그용 변수
        }
        
        System.out.println(">>> [DBManager] Current Request Method: " + req.getMethod());
        System.out.println(">>> [DBManager] Connecting to " + dbName + " Database...");

        if (ds == null) {
            throw new SQLException("DataSource not found for: " + dbName);
        }

        return ds.getConnection();
    }
}
