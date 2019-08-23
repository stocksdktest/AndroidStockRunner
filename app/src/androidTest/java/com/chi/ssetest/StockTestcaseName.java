package com.chi.ssetest;

public enum StockTestcaseName {
    QUOTE_REQUEST_EXAMPLE("TESTCASE_0"),
    QUOTE_REQUEST_EXAMPLE_2("TESTCASE_1");

    private String stringVal;
    StockTestcaseName(String numVal) {
        this.stringVal = numVal;
    }

    public String val() {
        return stringVal;
    }

    public static StockTestcaseName fromString(String text) {
        for (StockTestcaseName b : StockTestcaseName.values()) {
            if (b.stringVal.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
