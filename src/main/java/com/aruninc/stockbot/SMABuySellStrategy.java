/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aruninc.stockbot;

import java.util.Map;

/**
 *
 * @author arujohn
 */
public class SMABuySellStrategy {
    private final Map<String, Stock> stockMap;
    private final StockDate start;
    private final StockDate end;
    private double totalProfit = 0;
    private double totalCost = 0;

    SMABuySellStrategy(Map<String, Stock> stockMap, StockDate start, StockDate end) {
        this.stockMap = stockMap;
        this.start = start;
        this.end = end;
    }

    void compute() {

        for (String ticker : stockMap.keySet()) {
            Stock stock = stockMap.get(ticker);
            try {
                double subTotalProfit = 0;
                double initialPrice = 0;
                double previousValue = 0;
                double buyPrice = -1;
                Indicator sma = new SimpleMovingAverage(stock.closingPrices(), 30);

                for (StockDate day : StockDate.range(start, end)) {

                    final double currentValue = sma.valueAt(day) - stock.closingPrices().valueAt(day);
                    if (Math.signum(previousValue) != Math.signum(currentValue) && previousValue != 0) {
                        if (currentValue > 0) {
                            buyPrice = stock.closingPrices().valueAt(day);
                            if (initialPrice == 0) {
                                initialPrice = buyPrice;
                            }
                        } else {
                            if (buyPrice != -1) {
                                final double sellPrice = stock.closingPrices().valueAt(day);
                                double profit = sellPrice - buyPrice;
                                //System.out.printf("%s: Sell! Profit = %5g\n", day, profit);
                                subTotalProfit += profit;

                            }
                        }
                    }
                    previousValue = currentValue;

                }
                System.out.printf("Sub-Total Profit = %5g (%5g%%)\n", subTotalProfit, 100 * subTotalProfit / initialPrice);
                totalProfit += subTotalProfit;
                totalCost += initialPrice;
            } catch (StockValueNotAvailable x) {
                System.out.println("Skipping " + ticker + " because: " + x.getMessage());
            }

        }
        System.out.printf("Total Profit = %5g(%5g%%)\n", totalProfit, 100 * totalProfit / totalCost);
    }
}
