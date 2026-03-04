package dev.sample.report;

import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class RegionValidator {

    private static final Set<String> REGIONS = Set.of(
            "강원","경기","경남","경북","광주","대구","대전","부산","서울","세종",
            "울산","인천","전남","전북","제주","충남","충북"
    );

    public void validate(String region) {
        if (region == null || !REGIONS.contains(region)) {
            throw new IllegalArgumentException("invalid region: " + region);
        }
    }
}