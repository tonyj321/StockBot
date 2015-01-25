package com.aruninc.stockbot;

import java.util.Map;

/**
 *
 * @author tonyj
 */
public class MACDBuySellStrategy extends BuySellStrategy {


    MACDBuySellStrategy(Map<String, Stock> stockMap, StockDate start, StockDate end) {
        super(stockMap,start,end);
    }

    @Override
    void compute() {

        for (Stock stock : this.getStocks()) {
            try {
                double previousValue = 0;
                double buyPrice = -1;
                Indicator macd = new MovingAverageConvergenceDivergenceOscillator(stock.closingPrices(), 12, 26, 9);

                for (StockDate day : getDateRange()) {

                    final double currentValue = macd.valueAt(day);
                    if (Math.signum(previousValue) != Math.signum(currentValue) && previousValue != 0) {
                        if (currentValue > 0) {
                            buyPrice = stock.closingPrices().valueAt(day);
                            buy(stock, day, buyPrice, 1);
                        } else {
                            if (buyPrice != -1) {
                                final double sellPrice = stock.closingPrices().valueAt(day);
                                sell(stock, day, sellPrice, 1);

                            }
                        }
                    }
                    previousValue = currentValue;

                }
            } catch (StockValueNotAvailable x) {
                System.out.println("Skipping " + stock.getTicker() + " because: " + x.getMessage());
            }

        }
    }
    
}
