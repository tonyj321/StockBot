package com.aruninc.stockbot;

/**
 *
 * @author tonyj
 */
public class MovingAverageConvergenceDivergenceOscillator implements Indicator {
    private final ExponentialMovingAverage shortEMA;
    private final ExponentialMovingAverage longEMA;
    private final ExponentialMovingAverage indicatorEMA;
    private final MACDLine line;

    
    public MovingAverageConvergenceDivergenceOscillator(HistoricalValue values, int shortPeriod, int longPeriod, int emaPeriod) {
       shortEMA = new ExponentialMovingAverage(values, shortPeriod);
       longEMA = new ExponentialMovingAverage(values, longPeriod);
       line = new MACDLine();
       indicatorEMA = new ExponentialMovingAverage(line,emaPeriod);
    }
    
    @Override
    public double valueAt(StockDate date) {
        return  line.valueAt(date) - indicatorEMA.valueAt(date);
    }
    private class MACDLine implements HistoricalValue {

        @Override
        public double valueAt(StockDate date) {
            return shortEMA.valueAt(date) - longEMA.valueAt(date);
        }
        
    }
}
