package com.aruninc.stockbot;

import java.time.LocalDate;

/**
 *
 * @author tonyj
 */
public class SimpleMovingAverage {
    private final int nDays;

    public SimpleMovingAverage(int nDays) {
        this.nDays = nDays;
    }
    
    double getIndicatorValue(Stock stock, LocalDate date) {
        double result = 0;
        int daysFound = 0;
        for (int day = 0; daysFound<nDays; day++) {
            Stock.OpenHighLowCloseVolume value = stock.get(date.minusDays(day));
            if (value != null) {
                daysFound++;
                result += value.getClose();
            }
        }
        return result/nDays;
    }
}
