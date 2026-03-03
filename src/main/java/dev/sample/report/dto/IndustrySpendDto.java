package dev.sample.report.dto;

public class IndustrySpendDto {
    private final String categoryCode;
    private final String categoryName;
    private final long amount;

    public IndustrySpendDto(String categoryCode, String categoryName, long amount) {
        this.categoryCode = categoryCode;
        this.categoryName = categoryName;
        this.amount = amount;
    }

    public String getCategoryCode() { return categoryCode; }
    public String getCategoryName() { return categoryName; }
    public long getAmount() { return amount; }
}