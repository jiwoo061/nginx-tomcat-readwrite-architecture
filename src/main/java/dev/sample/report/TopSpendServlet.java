package dev.sample.report;

import dev.sample.report.dto.IndustrySpendDto;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class TopSpendServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String region = req.getParameter("region");

        resp.setContentType("application/json; charset=UTF-8");

        try {
            String basYh = reportService.getLatestBasYh(req);
            List<IndustrySpendDto> top = reportService.getTopByRegionLatest(req, region);

            resp.setStatus(200);
            resp.getWriter().write(buildJson(region, basYh, top));

        } catch (IllegalArgumentException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"success\":false,\"error\":\"" + escape(e.getMessage()) + "\"}");
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"success\":false,\"error\":\"internal server error\"}");
        }
    }

    private String buildJson(String region, String basYh, List<IndustrySpendDto> top) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"success\":true,");
        sb.append("\"region\":\"").append(escape(region)).append("\",");
        sb.append("\"basYh\":\"").append(escape(basYh)).append("\",");
        sb.append("\"top\":[");

        for (int i = 0; i < top.size(); i++) {
            IndustrySpendDto dto = top.get(i);
            if (i > 0) sb.append(",");
            sb.append("{")
              .append("\"categoryCode\":\"").append(escape(dto.getCategoryCode())).append("\",")
              .append("\"categoryName\":\"").append(escape(dto.getCategoryName())).append("\",")
              .append("\"amount\":").append(dto.getAmount())
              .append("}");
        }
        sb.append("]}");
        return sb.toString();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}
