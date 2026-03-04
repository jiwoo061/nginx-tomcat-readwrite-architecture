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

import dev.sample.auth.AuthConfig;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApplicationContextListener implements ServletContextListener {

    private static final String SPRING_CONTEXT_ATTR = "SPRING_CONTEXT"; // report ctx key

    private HikariDataSource sourceDS;
    private HikariDataSource replicaDS;

    // auth/login ctx
    private AnnotationConfigApplicationContext spring;

    // report ctx
    private AnnotationConfigApplicationContext springContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("컨텍스트 초기화됨");
        ServletContext ctx = sce.getServletContext();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // 1) Source DB (Write/Read)
        HikariConfig configSource = new HikariConfig();
        configSource.setJdbcUrl("jdbc:mysql://localhost:3306/card_db?serverTimezone=Asia/Seoul");
        configSource.setUsername("root");
        configSource.setPassword("1234");
        configSource.setPoolName("HikariPool-Source");
        sourceDS = new HikariDataSource(configSource);

        // 2) Replica DB (Read Only)
        HikariConfig configReplica = new HikariConfig();
        configReplica.setJdbcUrl("jdbc:mysql://localhost:3306/card_db?serverTimezone=Asia/Seoul");
        configReplica.setUsername("root");
        configReplica.setPassword("1234");
        configReplica.setPoolName("HikariPool-Replica");
        replicaDS = new HikariDataSource(configReplica);

        // ServletContext에 두 개의 DataSource 저장
        ctx.setAttribute("SOURCE_DS", sourceDS);
        ctx.setAttribute("REPLICA_DS", replicaDS);

        // ===== 3) auth/login용 Spring Context =====
        spring = new AnnotationConfigApplicationContext();
        spring.registerBean("sourceDS", DataSource.class, () -> sourceDS); // ✅ javax.sql.DataSource
        spring.register(AuthConfig.class);
        spring.refresh();
        ctx.setAttribute("SPRING_CTX", spring);

        System.out.println("[BOOT] DS ready: " + (sourceDS != null));
        System.out.println("[BOOT] SPRING_CTX ready: " + (spring != null) + ", beans=" + spring.getBeanDefinitionCount());

        // ===== 4) report용 Spring Context =====
        springContext = new AnnotationConfigApplicationContext();
        springContext.scan("dev.sample.report");
        springContext.refresh();
        ctx.setAttribute(SPRING_CONTEXT_ATTR, springContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("컨텍스트 종료됨: DB 커넥션 풀 자원 해제");

        if (spring != null) spring.close();
        if (springContext != null) springContext.close();

        if (sourceDS != null) sourceDS.close();
        if (replicaDS != null) replicaDS.close();

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

    // DS 꺼내기
    public static DataSource getSourceDataSource(ServletContext ctx) {
        return (DataSource) ctx.getAttribute("SOURCE_DS");
    }

    public static DataSource getReplicaDataSource(ServletContext ctx) {
        return (DataSource) ctx.getAttribute("REPLICA_DS");
    }

    // report ctx 꺼내기 (기존 유지)
    public static ApplicationContext getSpringContext(ServletContext ctx) {
        return (ApplicationContext) ctx.getAttribute(SPRING_CONTEXT_ATTR);
    }
}