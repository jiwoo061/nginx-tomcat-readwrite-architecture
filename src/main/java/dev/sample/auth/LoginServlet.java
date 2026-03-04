package dev.sample.auth;

import org.springframework.context.ApplicationContext;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class LoginServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
    	System.out.println("[LoginServlet] authService=" + authService);
    	ApplicationContext spring =
                (ApplicationContext) getServletContext().getAttribute("SPRING_CTX");
        this.authService = spring.getBean(AuthService.class);
    }

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

        try {
            User user = authService.login(loginId, password); // ✅ 여기서 DAO/DB 처리
            if (user == null) {
                resp.sendRedirect(req.getContextPath() + "/login.jsp?error=invalid");
                return;
            }

            HttpSession session = req.getSession(true);
            session.setAttribute("LOGIN_USER", user);
            session.setMaxInactiveInterval(30 * 60);

            resp.sendRedirect(req.getContextPath() + "/report");

        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}