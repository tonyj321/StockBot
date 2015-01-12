package com.aruninc.stockbot;

import java.time.DayOfWeek;
import java.util.Map;

/**
 *
 * @author tonyj
 */
public class BuyMondaySellFridayStrategy extends BuySellStrategy {

    BuyMondaySellFridayStrategy(Map<String, Stock> stockMap, StockDate start, StockDate end) {
        super(stockMap, start, end);
    }

    @Override
    void compute() {
        StockDate previousMonday = null;
        for (StockDate day : getDateRange()) {
            if (day.getDate().getDayOfWeek() == DayOfWeek.MONDAY) {
                previousMonday = day;
            }
            // We only care about weeks with a monday and a friday
            if (day.getDate().getDayOfWeek() == DayOfWeek.FRIDAY && previousMonday != null) {
                for (Stock stock : getStocks()) {
                    try {
                        final double buyPrice = stock.get(previousMonday).getOpen();
                        final double sellPrice = stock.get(day).getOpen();
                        buy(stock, previousMonday, buyPrice, 1);
                    } catch (StockValueNotAvailable x) {
                        // Just skip this stock
                    }
                }
                for (Stock stock : getStocks()) {
                    try {
                        final double buyPrice = stock.get(previousMonday).getOpen();
                        final double sellPrice = stock.get(day).getOpen();
                        sell(stock, day, sellPrice, 1);
                    } catch (StockValueNotAvailable x) {
                        // Just skip this stock
                    }
                }
                previousMonday = null;
            }
        }
    }
}
