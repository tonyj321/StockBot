package com.aruninc.stockbot;

import java.time.LocalDate;

/**
 *
 * @author tonyj
 */
public class MovingAverageConvergenceDivergenceOscillator {
    private final ExponentialMovingAverage shortEMA = new ExponentialMovingAverage(12);
    private final ExponentialMovingAverage longEMA = new ExponentialMovingAverage(26);
    private final ExponentialMovingAverage indicatorEMA = new ExponentialMovingAverage(9);

    
    public MovingAverageConvergenceDivergenceOscillator() {
    }
    
    double getIndicatorValue(Stock stock, LocalDate date) {
        return indicatorEMA.getIndicatorValue(stock, date) - (shortEMA.getIndicatorValue(stock,date) - longEMA.getIndicatorValue(stock, date));
    }
}
