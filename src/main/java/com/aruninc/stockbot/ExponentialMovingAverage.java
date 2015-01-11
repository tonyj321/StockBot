package com.aruninc.stockbot;

/**
 *
 * @author tonyj
 */
public class ExponentialMovingAverage implements Indicator {

    private final int nDays;
    private final int nBack = 250;
    private final SimpleMovingAverage simpleMovingAverage;
    private final HistoricalValue values;

    public ExponentialMovingAverage(HistoricalValue values, int nDays) {
        this.nDays = nDays;
        this.values = values;
        simpleMovingAverage = new SimpleMovingAverage(values, nDays);
    }

    @Override
    public double valueAt(StockDate date) {
        StockDate start = date.minus(nBack);
        double ema = simpleMovingAverage.valueAt(start);
        double multiplier = 2.0 / (nDays + 1);
        for (StockDate day : StockDate.range(start, date)) {
            ema = (values.valueAt(day) - ema) * multiplier + ema;
        }
        return ema;
    }
}
