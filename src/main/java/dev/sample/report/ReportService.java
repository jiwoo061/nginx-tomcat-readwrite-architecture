package dev.sample.report;

import dev.sample.report.dto.IndustrySpendDto;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service
public class ReportService {

	private final RegionValidator regionValidator;
	private final ReportReadDao reportReadDao;

	public ReportService(ReportReadDao reportReadDao, RegionValidator regionValidator) {
		this.reportReadDao = reportReadDao;
		this.regionValidator = regionValidator;
	}

	public String getLatestBasYh(HttpServletRequest req) {
		return reportReadDao.findLatestBasYh(req);
	}

	public List<IndustrySpendDto> getTopByRegionLatest(HttpServletRequest req, String region) {
		regionValidator.validate(region);
		return reportReadDao.findTopByRegionLatest(req, region);
	}
}
