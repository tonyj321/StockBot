package com.aruninc.stockbot;

import java.util.Map;

/**
 *
 * @author arujohn Runs through all the stocks on a current day, decides which
 * one has had the greatest change in value since yesterday, and buys it It
 * should not sell the old stock until
 */
public class SMABestBuySellStrategy extends BuySellStrategy {

    SMABestBuySellStrategy(Map<String, Stock> stockMap, StockDate start, StockDate end) {
        super(stockMap, start, end);
    }

    @Override
    void compute() {

        for (StockDate today : getDateRange()) {

            StockDate yesterday = today.minus(1);
            double maxSwing = 0;
            Stock maxStock = null;

            for (Stock stock : getStocks()) {
                try {

                    Indicator sma = new SimpleMovingAverage(stock.closingPrices(), 30);
                    final double previousValue = stock.closingPrices().valueAt(yesterday) - sma.valueAt(yesterday);
                    final double currentValue = stock.closingPrices().valueAt(today) - sma.valueAt(today);

                    if (Math.signum(previousValue) != Math.signum(currentValue)) {
                        if (currentValue > 0) {
                            if (currentValue - previousValue > maxSwing) {
                                maxSwing = currentValue - previousValue;
                                maxStock = stock;
                            }

                        } else {
                            int n = getCurrentlyOwned(stock);
                            if (n>0) {
                                sell(stock,today,stock.closingPrices().valueAt(today),n);
                            }
                        }
                    }

                } catch (StockValueNotAvailable x) {
                    //System.out.println("Skipping " + stock.getTicker() + " because: " + x.getMessage());
                }
            }
            if (maxStock!=null) {
                buy(maxStock,today,maxStock.closingPrices().valueAt(today),1);
            }
        }
    }

}
