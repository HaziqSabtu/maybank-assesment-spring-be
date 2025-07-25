package com.assesment.maybank.spring_be.enums;

public enum CountryCode {
    BRUNEI("BN", "Brunei", "BND"),
    CAMBODIA("KH", "Cambodia", "KHR"),
    EAST_TIMOR("TL", "East Timor", "USD"),
    INDONESIA("ID", "Indonesia", "IDR"),
    LAOS("LA", "Laos", "LAK"),
    MALAYSIA("MY", "Malaysia", "MYR"),
    MYANMAR("MM", "Myanmar", "MMK"),
    PHILIPPINES("PH", "Philippines", "PHP"),
    SINGAPORE("SG", "Singapore", "SGD"),
    THAILAND("TH", "Thailand", "THB"),
    VIETNAM("VN", "Vietnam", "VND"),
    UNSUPPORTED("XX", "Unsupported", "XXX");

    private final String code;
    private final String name;
    private final String currencyCode;

    CountryCode(String code, String name, String currencyCode) {
        this.code = code;
        this.name = name;
        this.currencyCode = currencyCode;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public static CountryCode fromCode(String code) {
        for (CountryCode country : values()) {
            if (country.code.equalsIgnoreCase(code)) {
                return country;
            }
        }
        return UNSUPPORTED;
    }
}
