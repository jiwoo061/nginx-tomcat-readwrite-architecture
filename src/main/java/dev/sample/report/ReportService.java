package dev.sample.report;

import dev.sample.report.dto.IndustrySpendDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

public class ReportService {

    private static final Set<String> REGIONS = Set.of(
            "강원","경기","경남","경북","광주","대구","대전","부산","서울","세종",
            "울산","인천","전남","전북","제주","충남","충북"
    );

    private final ReportReadDao reportReadDao = new ReportReadDao();

    public String getLatestBasYh(HttpServletRequest req) {
        return reportReadDao.findLatestBasYh(req);
    }

    public List<IndustrySpendDto> getTopByRegionLatest(HttpServletRequest req, String region) {
        if (region == null || !REGIONS.contains(region)) {
            throw new IllegalArgumentException("invalid region: " + region);
        }
        return reportReadDao.findTopByRegionLatest(req, region);
    }
}
