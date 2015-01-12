package com.aruninc.stockbot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

/**
 *
 * @author tonyj
 */
public abstract class BuySellStrategy {

    private final Map<String, Stock> stockMap;
    private final StockDate start;
    private final StockDate end;
    private double totalMoney = 0;
    private double minMoney = 0;
    private List<Lot> lots = new ArrayList<>();

    BuySellStrategy(Map<String, Stock> stockMap, StockDate start, StockDate end) {
        this.stockMap = stockMap;
        this.start = start;
        this.end = end;
    }

    void buy(Stock stock, StockDate date, double price, int nStocks) {
        totalMoney -= price * nStocks;
        if (totalMoney<minMoney) minMoney = totalMoney;
    }

    void sell(Stock stock, StockDate date, double price, int nStocks) {
        totalMoney += price * nStocks;
    }

    abstract void compute();
    
    void report() {
        System.out.printf("Total Money %5g\n",totalMoney);
        System.out.printf("Minimum Money %5g\n",minMoney);
        System.out.printf("%% Profit %5g\n",100*totalMoney/-minMoney);    
    }

    NavigableSet<StockDate> getDateRange() {
        return StockDate.range(start, end);
    }

    Collection<Stock> getStocks() {
        return stockMap.values();
    }
    
    private class Lot {
        private final Stock stock;
        private final StockDate date;
        private final double price;
        private int nStocks;

        public Lot(Stock stock, StockDate date, double price, int nStocks) {
            this.stock = stock;
            this.date = date;
            this.price = price;
            this.nStocks = nStocks;
        }
        
        
    }
}
