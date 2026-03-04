package dev.sample.auth;

import javax.servlet.http.*;
import java.io.IOException;

public class LogoutServlet extends HttpServlet {

    private AuthService authService;

    @Override
    public void init() {
        authService = new AuthService(null);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        HttpSession session = req.getSession(false);

        authService.logout(session);

        resp.sendRedirect(req.getContextPath() + "/login.jsp");
    }
}