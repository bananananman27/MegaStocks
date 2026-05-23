package com.weston.megastocks.market;

public final class StockData {
    private final String symbol;
    private final String name;
    private double price;
    private double previousPrice;
    private String lastNews;

    public StockData(String symbol, String name, double price) {
        this.symbol = symbol.toUpperCase();
        this.name = name;
        this.price = price;
        this.previousPrice = price;
        this.lastNews = "Market opened.";
    }

    public String symbol() { return symbol; }
    public String name() { return name; }
    public double price() { return price; }
    public void price(double price) { this.price = price; }
    public double previousPrice() { return previousPrice; }
    public void previousPrice(double previousPrice) { this.previousPrice = previousPrice; }
    public String lastNews() { return lastNews; }
    public void lastNews(String lastNews) { this.lastNews = lastNews; }
    public double changePercent() {
        if (previousPrice <= 0.0) return 0.0;
        return ((price - previousPrice) / previousPrice) * 100.0;
    }
}
