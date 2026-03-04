package dev.sample.report;

import dev.sample.DBManager;
import dev.sample.report.dto.IndustrySpendDto;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportReadDao {

    private static final String SQL_LATEST_BAS_YH =
            "SELECT MAX(BAS_YH) AS bas_yh FROM CARD_TRANSACTION";

    private static final String SQL_TOP_BY_REGION_LATEST =
            "WITH filtered AS ( " +
            "  SELECT * " +
            "  FROM CARD_TRANSACTION " +
            "  WHERE HOUS_SIDO_NM = ? " +
            "    AND BAS_YH = (SELECT MAX(BAS_YH) FROM CARD_TRANSACTION) " +
            "), agg AS ( " +
            "  SELECT " +
            "    COALESCE(SUM(FUNITR_AM), 0) AS FUNITR_AM, " +
            "    COALESCE(SUM(TRVL_AM), 0) AS TRVL_AM, " +
            "    COALESCE(SUM(APPLNC_AM), 0) AS APPLNC_AM, " +
            "    COALESCE(SUM(FUEL_AM), 0) AS FUEL_AM, " +
            "    COALESCE(SUM(HLTHFS_AM), 0) AS HLTHFS_AM, " +
            "    COALESCE(SUM(SVC_AM), 0) AS SVC_AM, " +
            "    COALESCE(SUM(BLDMNG_AM), 0) AS BLDMNG_AM, " +
            "    COALESCE(SUM(DISTBNP_AM), 0) AS DISTBNP_AM, " +
            "    COALESCE(SUM(ARCHIT_AM), 0) AS ARCHIT_AM, " +
            "    COALESCE(SUM(DISTBP_AM), 0) AS DISTBP_AM, " +
            "    COALESCE(SUM(OPTIC_AM), 0) AS OPTIC_AM, " +
            "    COALESCE(SUM(GROCERY_AM), 0) AS GROCERY_AM, " +
            "    COALESCE(SUM(AGRICTR_AM), 0) AS AGRICTR_AM, " +
            "    COALESCE(SUM(HOS_AM), 0) AS HOS_AM, " +
            "    COALESCE(SUM(LEISURE_S_AM), 0) AS LEISURE_S_AM, " +
            "    COALESCE(SUM(CLOTH_AM), 0) AS CLOTH_AM, " +
            "    COALESCE(SUM(LEISURE_P_AM), 0) AS LEISURE_P_AM, " +
            "    COALESCE(SUM(RESTRNT_AM), 0) AS RESTRNT_AM, " +
            "    COALESCE(SUM(CULTURE_AM), 0) AS CULTURE_AM, " +
            "    COALESCE(SUM(AUTOMNT_AM), 0) AS AUTOMNT_AM, " +
            "    COALESCE(SUM(SANIT_AM), 0) AS SANIT_AM, " +
            "    COALESCE(SUM(AUTOSL_AM), 0) AS AUTOSL_AM, " +
            "    COALESCE(SUM(INSU_AM), 0) AS INSU_AM, " +
            "    COALESCE(SUM(KITWR_AM), 0) AS KITWR_AM, " +
            "    COALESCE(SUM(OFFCOM_AM), 0) AS OFFCOM_AM, " +
            "    COALESCE(SUM(FABRIC_AM), 0) AS FABRIC_AM, " +
            "    COALESCE(SUM(BOOK_AM), 0) AS BOOK_AM, " +
            "    COALESCE(SUM(ACDM_AM), 0) AS ACDM_AM, " +
            "    COALESCE(SUM(RPR_AM), 0) AS RPR_AM, " +
            "    COALESCE(SUM(MBRSHOP_AM), 0) AS MBRSHOP_AM, " +
            "    COALESCE(SUM(HOTEL_AM), 0) AS HOTEL_AM, " +
            "    COALESCE(SUM(GOODS_AM), 0) AS GOODS_AM " +
            "  FROM filtered " +
            ") " +
            "SELECT category_code, category_name, total_amount " +
            "FROM (" +
            "  SELECT 'FUNITR_AM' AS category_code, '가구' AS category_name, FUNITR_AM AS total_amount FROM agg " +
            "  UNION ALL SELECT 'TRVL_AM', '여행업', TRVL_AM FROM agg " +
            "  UNION ALL SELECT 'APPLNC_AM', '가전제품', APPLNC_AM FROM agg " +
            "  UNION ALL SELECT 'FUEL_AM', '연료판매', FUEL_AM FROM agg " +
            "  UNION ALL SELECT 'HLTHFS_AM', '건강식품', HLTHFS_AM FROM agg " +
            "  UNION ALL SELECT 'SVC_AM', '용역서비스', SVC_AM FROM agg " +
            "  UNION ALL SELECT 'BLDMNG_AM', '건물및시설관리', BLDMNG_AM FROM agg " +
            "  UNION ALL SELECT 'DISTBNP_AM', '유통업 비영리', DISTBNP_AM FROM agg " +
            "  UNION ALL SELECT 'ARCHIT_AM', '건축/자재', ARCHIT_AM FROM agg " +
            "  UNION ALL SELECT 'DISTBP_AM', '유통업 영리', DISTBP_AM FROM agg " +
            "  UNION ALL SELECT 'OPTIC_AM', '광학제품', OPTIC_AM FROM agg " +
            "  UNION ALL SELECT 'GROCERY_AM', '음식료품', GROCERY_AM FROM agg " +
            "  UNION ALL SELECT 'AGRICTR_AM', '농업', AGRICTR_AM FROM agg " +
            "  UNION ALL SELECT 'HOS_AM', '의료기관', HOS_AM FROM agg " +
            "  UNION ALL SELECT 'LEISURE_S_AM', '레져업소', LEISURE_S_AM FROM agg " +
            "  UNION ALL SELECT 'CLOTH_AM', '의류', CLOTH_AM FROM agg " +
            "  UNION ALL SELECT 'LEISURE_P_AM', '레져용품', LEISURE_P_AM FROM agg " +
            "  UNION ALL SELECT 'RESTRNT_AM', '일반/휴게음식', RESTRNT_AM FROM agg " +
            "  UNION ALL SELECT 'CULTURE_AM', '문화/취미', CULTURE_AM FROM agg " +
            "  UNION ALL SELECT 'AUTOMNT_AM', '자동차정비/유지', AUTOMNT_AM FROM agg " +
            "  UNION ALL SELECT 'SANIT_AM', '보건/위생', SANIT_AM FROM agg " +
            "  UNION ALL SELECT 'AUTOSL_AM', '자동차판매', AUTOSL_AM FROM agg " +
            "  UNION ALL SELECT 'INSU_AM', '보험', INSU_AM FROM agg " +
            "  UNION ALL SELECT 'KITWR_AM', '주방용품', KITWR_AM FROM agg " +
            "  UNION ALL SELECT 'OFFCOM_AM', '사무/통신기기', OFFCOM_AM FROM agg " +
            "  UNION ALL SELECT 'FABRIC_AM', '직물', FABRIC_AM FROM agg " +
            "  UNION ALL SELECT 'BOOK_AM', '서적/문구', BOOK_AM FROM agg " +
            "  UNION ALL SELECT 'ACDM_AM', '학원', ACDM_AM FROM agg " +
            "  UNION ALL SELECT 'RPR_AM', '수리서비스', RPR_AM FROM agg " +
            "  UNION ALL SELECT 'MBRSHOP_AM', '회원제형태업소', MBRSHOP_AM FROM agg " +
            "  UNION ALL SELECT 'HOTEL_AM', '숙박업', HOTEL_AM FROM agg " +
            "  UNION ALL SELECT 'GOODS_AM', '신변잡화', GOODS_AM FROM agg " +
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
