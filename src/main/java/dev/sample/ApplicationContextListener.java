package dev.sample;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class ApplicationContextListener implements ServletContextListener {

	private HikariDataSource sourceDS;
	private HikariDataSource replicaDS;

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("컨텍스트 초기화됨");
		ServletContext ctx = sce.getServletContext();

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		HikariConfig configSource = new HikariConfig();
		// 필수 설정값(별도의 설정파일로 분리 가능, ex. jdbc.properties)
		// 1. Source DB 설정 (Write/Read)
		configSource.setJdbcUrl("jdbc:mysql://localhost:3308/card_db?serverTimezone=Asia/Seoul");
		configSource.setUsername("root");
		configSource.setPassword("1234");
		configSource.setPoolName("HikariPool-Source");
		sourceDS = new HikariDataSource(configSource);

		// 2. Replica DB 설정 (Read Only)
		HikariConfig configReplica = new HikariConfig();
		configReplica.setJdbcUrl("jdbc:mysql://localhost:3309/card_db?serverTimezone=Asia/Seoul");
		configReplica.setUsername("root");
		configReplica.setPassword("1234");
		configReplica.setPoolName("HikariPool-Replica");
		replicaDS = new HikariDataSource(configReplica);

		// 선택 설정값 예시
//        config.setMaximumPoolSize(10);
//        config.setMinimumIdle(2);
//        config.setConnectionTimeout(3000);
//        config.setIdleTimeout(600000);
//        config.setMaxLifetime(1800000);

//		ds = new HikariDataSource(config);

//		ctx.setAttribute("DATA_SOURCE", ds);

		// ServletContext에 두 개의 DataSource를 모두 저장
		ctx.setAttribute("SOURCE_DS", sourceDS);
		ctx.setAttribute("REPLICA_DS", replicaDS);
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("컨텍스트 종료됨: DB 커넥션 풀 자원 해제");
		if (sourceDS != null)
			sourceDS.close();
		if (replicaDS != null)
			replicaDS.close();

		// Tomcat 재배포/중지 시 MySQL cleanup thread 누수 경고 방지
		try {
			AbandonedConnectionCleanupThread.checkedShutdown();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 현재 웹앱 클래스로더에서 등록한 JDBC 드라이버 정리
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver d = drivers.nextElement();
			if (d.getClass().getClassLoader() == cl) {
				try {
					DriverManager.deregisterDriver(d);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	// 상황에 맞는 DataSource를 가져오기 위한 static 메서드
	public static DataSource getSourceDataSource(ServletContext ctx) {
		return (DataSource) ctx.getAttribute("SOURCE_DS");
	}

	public static DataSource getReplicaDataSource(ServletContext ctx) {
		return (DataSource) ctx.getAttribute("REPLICA_DS");
	}
}
