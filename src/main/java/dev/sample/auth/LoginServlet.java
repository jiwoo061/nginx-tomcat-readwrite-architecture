package dev.sample.auth;

import dev.sample.DBManager;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");

        String loginId = req.getParameter("loginId");
        String password = req.getParameter("password");

        if (loginId == null || password == null || loginId.isBlank() || password.isBlank()) {
            resp.sendRedirect(req.getContextPath() + "/login.jsp?error=empty");
            return;
        }

        String sql = "SELECT user_id, login_id, password, name FROM users WHERE login_id = ?";

        try (Connection con = DBManager.getConnection(req);
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, loginId);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    resp.sendRedirect(req.getContextPath() + "/login.jsp?error=invalid");
                    return;
                }

                String dbPw = rs.getString("password");
                if (!password.equals(dbPw)) {
                    resp.sendRedirect(req.getContextPath() + "/login.jsp?error=invalid");
                    return;
                }

                User user = new User(
                        rs.getLong("user_id"),
                        rs.getString("login_id"),
                        rs.getString("name")
                );

                HttpSession session = req.getSession(true);
                session.setAttribute("LOGIN_USER", user);
                session.setMaxInactiveInterval(30 * 60); // 30분

                resp.sendRedirect(req.getContextPath() + "/report");
            }

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
