package com.chi.ssetest;

public enum StockTestcaseName {
    QUOTE_REQUEST_EXAMPLE("TESTCASE_0"),
    AHLISTTEST_1("AHLIST_1"),
    AHLISTTEST_2("AHLIST_2"),
    AHQUOTETEST_1("AHQUOTE_1"),
    CHARTV2TEST_1("CHARTV2TEST_1"),
    CHARTV2TEST_2("CHARTV2TEST_2"),
    CHARTV2TEST_3("CHARTV2TEST_3"),
    CHARTV2TEST_4("CHARTV2TEST_4"),
    CHARTV2TEST_5("CHARTV2TEST_5"),
    CHARTV2TEST_6("CHARTV2TEST_6"),
    OHLCV3TEST_1("OHLCV3_1"),
    OHLCV3TEST_2("OHLCV3_2"),
    OHLCV3TEST_3("OHLCV3_3"),
    OHLCV3TEST_4("OHLCV3_4"),
    OHLCV3TEST_5("OHLCV3_5"),
    L2TICKDETAILV2TEST_1("L2TICKDETAILV2_1"),
    L2TICKV2TEST_1("L2TICKV2_1"),
    PLATEINDEXQUOTETEST_1("PLATEINDEXQUOTE_1"),
    PLATEINDEXQUOTETEST_2("PLATEINDEXQUOTE_2"),
    QUOTEDETAILTEST_1("QUOTEDETAIL_1"),
    QUOTEDETAILTEST_2("QUOTEDETAIL_2"),
    QUOTEDETAILTEST_3("QUOTEDETAIL_3"),
    QUOTETEST_1("QUOTE_1"),
    QUOTETEST_2("QUOTE_2"),
    TICKTEST_1("TICK_1");

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
