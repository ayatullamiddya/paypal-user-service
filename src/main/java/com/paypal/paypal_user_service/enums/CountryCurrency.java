package com.paypal.paypal_user_service.enums;

import lombok.Data;
import lombok.NoArgsConstructor;


public enum CountryCurrency {

    INDIA("India", "INR", "Indian Rupee"),
    USA("United States", "USD", "US Dollar"),
    UK("United Kingdom", "GBP", "Pound Sterling"),
    UAE("United Arab Emirates", "AED", "UAE Dirham"),
    JAPAN("Japan", "JPY", "Japanese Yen"),
    EUROPE("European Union", "EUR", "Euro");

    private final String countryName;
    private final String currencyCode;
    private final String currencyName;

    CountryCurrency(String countryName, String currencyCode, String currencyName) {
        this.countryName = countryName;
        this.currencyCode = currencyCode;
        this.currencyName = currencyName;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyName() {
        return currencyName;
    }
}
