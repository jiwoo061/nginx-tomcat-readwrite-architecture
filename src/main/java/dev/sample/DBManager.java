package dev.sample;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

public class DBManager {
    public static Connection getConnection(HttpServletRequest req) throws SQLException {
        ServletContext ctx = req.getServletContext();
        DataSource ds;
        
        // 1. 요청 메서드에 따른 분기
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            ds = (DataSource) ctx.getAttribute("REPLICA_DS");
        } else {
            ds = (DataSource) ctx.getAttribute("SOURCE_DS");
        }
        
        if (ds == null) {
            throw new SQLException("DataSource not found.");
        }

        // 2. 실제 커넥션을 먼저 가져옵니다.
        Connection con = ds.getConnection();

        // 3. 연결된 Connection 객체로부터 실제 물리적 주소 정보를 읽어옵니다.
        DatabaseMetaData meta = con.getMetaData();
        String actualUrl = meta.getURL(); 

        // 4. 로그 출력
        System.out.println("--- DB Connection Info ---");
        System.out.println("Request Method : " + req.getMethod());
        System.out.println("Real Connect URL : " + actualUrl);
        System.out.println("--------------------------");

        return con;
    }
}