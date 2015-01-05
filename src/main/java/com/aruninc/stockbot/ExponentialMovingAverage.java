package com.aruninc.stockbot;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

/**
 *
 * @author tonyj
 */
public class ExponentialMovingAverage {

    private final int nDays;
    private final int nBack = 150;
    private final SimpleMovingAverage simpleMovingAverage;

    public ExponentialMovingAverage(int nDays) {
        this.nDays = nDays;
        simpleMovingAverage = new SimpleMovingAverage(nDays);
    }

    double getIndicatorValue(Stock stock, LocalDate date) {
        LocalDate[] dates = new LocalDate[nBack];
        int daysFound = 0;
        for (int day = 0; daysFound < nBack; day++) {
            Stock.OpenHighLowCloseVolume value = stock.get(date.minusDays(day));
            if (value != null) {
               dates[daysFound] = date.minusDays(day);
               daysFound++;
            }
        }
        Collections.reverse(Arrays.asList(dates));
        // We have found the starting
        double ema = simpleMovingAverage.getIndicatorValue(stock, dates[0]);
        double multiplier = 2.0 / (nDays + 1);
        for (LocalDate day : dates) {
            Stock.OpenHighLowCloseVolume value = stock.get(day);
            ema = (value.getClose() - ema) * multiplier + ema;
        }
        return ema;
    }
}
