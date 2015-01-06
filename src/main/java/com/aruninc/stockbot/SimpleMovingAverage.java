package com.aruninc.stockbot;

/**
 *
 * @author tonyj
 */
public class SimpleMovingAverage implements Indicator {
    private final int nDays;
    private final HistoricalValue values;

    public SimpleMovingAverage(HistoricalValue values, int nDays) {
        this.nDays = nDays;
        this.values = values;
    }
    
    @Override
    public double valueAt(StockDate date) {
        double result = 0;
        for (StockDate day : StockDate.range(date.minus(nDays-1), date)) {
            result += values.valueAt(day);
        }
        return result/nDays;
    }
}
