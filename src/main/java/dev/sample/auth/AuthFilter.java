package dev.sample.auth;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.IOException;

//@WebFilter(urlPatterns = {"/report/*", "/api/*"})
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        HttpSession session = req.getSession(false);
        Object loginUser = (session == null) ? null : session.getAttribute("LOGIN_USER");

        if (loginUser == null) {
            String uri = req.getRequestURI();
            boolean isReportApi = uri != null && uri.startsWith(req.getContextPath() + "/report/");
            String accept = req.getHeader("Accept");
            boolean wantsJson = accept != null && accept.contains("application/json");

            if (isReportApi || wantsJson) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.setContentType("application/json; charset=UTF-8");
                resp.getWriter().write("{\"success\":false,\"error\":\"unauthorized\"}");
            } else {
                resp.sendRedirect(req.getContextPath() + "/login.jsp");
            }
            return;
        }

        chain.doFilter(request, response);
    }
}
