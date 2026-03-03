package dev.sample.report;

import dev.sample.DBManager;
import dev.sample.report.dto.IndustrySpendDto;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReportReadDao {

    private static final String SQL_LATEST_BAS_YH =
            "SELECT MAX(BAS_YH) AS bas_yh FROM CARD_TRANSACTION";

    private static final String SQL_TOP_BY_REGION_LATEST =
            "WITH params AS ( " +
            "  SELECT ? AS region, (SELECT MAX(BAS_YH) FROM CARD_TRANSACTION) AS latest_bas_yh " +
            ") " +
            "SELECT category_code, category_name, total_amount " +
            "FROM (" +
            "  SELECT 'FUNITR_AM' AS category_code, '가구' AS category_name, SUM(COALESCE(ct.FUNITR_AM, 0)) AS total_amount FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'TRVL_AM', '여행업', SUM(COALESCE(ct.TRVL_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'APPLNC_AM', '가전제품', SUM(COALESCE(ct.APPLNC_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'FUEL_AM', '연료판매', SUM(COALESCE(ct.FUEL_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'HLTHFS_AM', '건강식품', SUM(COALESCE(ct.HLTHFS_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'SVC_AM', '용역서비스', SUM(COALESCE(ct.SVC_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'BLDMNG_AM', '건물및시설관리', SUM(COALESCE(ct.BLDMNG_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'DISTBNP_AM', '유통업 비영리', SUM(COALESCE(ct.DISTBNP_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'ARCHIT_AM', '건축/자재', SUM(COALESCE(ct.ARCHIT_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'DISTBP_AM', '유통업 영리', SUM(COALESCE(ct.DISTBP_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'OPTIC_AM', '광학제품', SUM(COALESCE(ct.OPTIC_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'GROCERY_AM', '음식료품', SUM(COALESCE(ct.GROCERY_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'AGRICTR_AM', '농업', SUM(COALESCE(ct.AGRICTR_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'HOS_AM', '의료기관', SUM(COALESCE(ct.HOS_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'LEISURE_S_AM', '레져업소', SUM(COALESCE(ct.LEISURE_S_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'CLOTH_AM', '의류', SUM(COALESCE(ct.CLOTH_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'LEISURE_P_AM', '레져용품', SUM(COALESCE(ct.LEISURE_P_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'RESTRNT_AM', '일반/휴게음식', SUM(COALESCE(ct.RESTRNT_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'CULTURE_AM', '문화/취미', SUM(COALESCE(ct.CULTURE_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'AUTOMNT_AM', '자동차정비/유지', SUM(COALESCE(ct.AUTOMNT_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'SANIT_AM', '보건/위생', SUM(COALESCE(ct.SANIT_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'AUTOSL_AM', '자동차판매', SUM(COALESCE(ct.AUTOSL_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'INSU_AM', '보험', SUM(COALESCE(ct.INSU_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'KITWR_AM', '주방용품', SUM(COALESCE(ct.KITWR_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'OFFCOM_AM', '사무/통신기기', SUM(COALESCE(ct.OFFCOM_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'FABRIC_AM', '직물', SUM(COALESCE(ct.FABRIC_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'BOOK_AM', '서적/문구', SUM(COALESCE(ct.BOOK_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'ACDM_AM', '학원', SUM(COALESCE(ct.ACDM_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'RPR_AM', '수리서비스', SUM(COALESCE(ct.RPR_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'MBRSHOP_AM', '회원제형태업소', SUM(COALESCE(ct.MBRSHOP_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'HOTEL_AM', '숙박업', SUM(COALESCE(ct.HOTEL_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            "  UNION ALL SELECT 'GOODS_AM', '신변잡화', SUM(COALESCE(ct.GOODS_AM, 0)) FROM CARD_TRANSACTION ct JOIN params p ON ct.HOUS_SIDO_NM = p.region AND ct.BAS_YH = p.latest_bas_yh " +
            ") t " +
            "ORDER BY total_amount DESC " +
            "LIMIT 5";

    public String findLatestBasYh(HttpServletRequest req) {
        try (Connection conn = DBManager.getConnection(req);
             PreparedStatement ps = conn.prepareStatement(SQL_LATEST_BAS_YH);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) return rs.getString("bas_yh");
            return "";

        } catch (Exception e) {
            throw new RuntimeException("findLatestBasYh failed", e);
        }
    }

    public List<IndustrySpendDto> findTopByRegionLatest(HttpServletRequest req, String region) {
        List<IndustrySpendDto> list = new ArrayList<>();

        try (Connection conn = DBManager.getConnection(req);
             PreparedStatement ps = conn.prepareStatement(SQL_TOP_BY_REGION_LATEST)) {

            ps.setString(1, region);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new IndustrySpendDto(
                            rs.getString("category_code"),
                            rs.getString("category_name"),
                            rs.getLong("total_amount")
                    ));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("findTopByRegionLatest failed", e);
        }

        return list;
    }
}
