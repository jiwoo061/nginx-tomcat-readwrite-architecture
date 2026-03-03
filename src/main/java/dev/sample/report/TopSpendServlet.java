package dev.sample.report;

import dev.sample.report.dto.IndustrySpendDto;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TopSpendServlet extends HttpServlet {

    private final ReportService reportService = new ReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String region = normalizeRegion(req);

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
            e.printStackTrace();
            resp.setStatus(500);
            Throwable root = (e.getCause() != null) ? e.getCause() : e;
            String msg = root.getMessage();
            String err = root.getClass().getSimpleName() + ": " + (msg == null ? "unknown" : msg);
            resp.getWriter().write("{\"success\":false,\"error\":\"" + escape(err) + "\"}");
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

    private String normalizeRegion(HttpServletRequest req) {
        String region = req.getParameter("region");
        if (region != null) {
            region = region.trim();
        }

        if (region != null && !region.isEmpty()) {
            return region;
        }

        String qs = req.getQueryString();
        if (qs == null || qs.isEmpty()) {
            return region;
        }

        for (String pair : qs.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;

            String key = pair.substring(0, idx);
            if (!"region".equals(key)) continue;

            String raw = pair.substring(idx + 1);
            String decoded = URLDecoder.decode(raw, StandardCharsets.UTF_8);
            return decoded == null ? null : decoded.trim();
        }
        return region;
    }
}
