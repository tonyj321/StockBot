package com.aruninc.stockbot;

/**
 * A value which has a history over time.
 * @author tonyj
 */
public interface HistoricalValue {
    double valueAt(StockDate date);
}
