package com.aruninc.stockbot;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 *
 * @author tonyj
 */
public abstract class BuySellStrategy {

    private final Map<String, Stock> stockMap;
    private final StockDate start;
    private final StockDate end;
    private final NavigableSet<Lot> buySell = new TreeSet<>(new LotDateComparator());
    private final boolean allowShorts = false;

    BuySellStrategy(Map<String, Stock> stockMap, StockDate start, StockDate end) {
        this.stockMap = stockMap;
        this.start = start;
        this.end = end;
    }

    void buy(Stock stock, StockDate date, double price, int nStocks) {
        if (date.compareTo(start) < 0 || date.compareTo(end) > 0) {
            throw new RuntimeException("Date not in range");
        }
        buySell.add(Lot.buy(stock, date, price, nStocks));
    }

    void sell(Stock stock, StockDate date, double price, int nStocks) {
        if (date.compareTo(start) < 0 || date.compareTo(end) > 0) {
            throw new RuntimeException("Date not in range");
        }
        buySell.add(Lot.sell(stock, date, price, nStocks));
    }

    abstract void compute();

    void report() {
        double totalMoney = 0;
        double maxMoney = 0;
        double minMoney = 0;

        Map<Stock, Count> held = new HashMap<>();
        for (Lot lot : buySell) {
            Count c = held.get(lot.stock);
            if (c == null) {
                c = new Count();
                held.put(lot.stock, c);
            }
            if (lot.buy) {
                c.plus(lot.nStocks);
                totalMoney -= lot.price * lot.nStocks;
            } else {
                int n = c.minus(lot.nStocks);
                if (!allowShorts && n < 0) {
                    throw new RuntimeException("Sell without but at " + lot.date + " for " + lot.stock);
                }
                totalMoney += lot.price * lot.nStocks;
            }
            if (totalMoney > maxMoney) {
                maxMoney = totalMoney;
            }
            if (totalMoney < minMoney) {
                minMoney = totalMoney;
            }
        }
        // If there are left over stocks, we need to sell (buy) them
        for (Map.Entry<Stock, Count> holding : held.entrySet()) {
            try {
                int c = holding.getValue().getCount();
                if (c != 0) {
                    totalMoney += c * holding.getKey().get(end).getClose();
                }
            } catch (StockValueNotAvailable x) {
                // Assume worthless
            }
        }

        System.out.printf("Total Money %5g\n", totalMoney);
        System.out.printf("Minimum Money %5g\n", minMoney);
        System.out.printf("Maximum Money %5g\n", maxMoney);

        System.out.printf("%% Profit %5g\n", 100 * totalMoney / -minMoney);
    }

    NavigableSet<StockDate> getDateRange() {
        return StockDate.range(start, end);
    }

    Collection<Stock> getStocks() {
        return stockMap.values();
    }

    Map<String, Stock> getStockMap() {
        return stockMap;
    }

    private static class Lot {

        private final Stock stock;
        private final StockDate date;
        private final double price;
        private final int nStocks;
        boolean buy;

        private Lot(boolean buy, Stock stock, StockDate date, double price, int nStocks) {
            this.buy = buy;
            this.stock = stock;
            this.date = date;
            this.price = price;
            this.nStocks = nStocks;
        }

        static Lot buy(Stock stock, StockDate date, double price, int nStocks) {
            return new Lot(true, stock, date, price, nStocks);
        }

        static Lot sell(Stock stock, StockDate date, double price, int nStocks) {
            return new Lot(false, stock, date, price, nStocks);
        }
    }

    private static class LotDateComparator implements Comparator<Lot> {

        @Override
        public int compare(Lot l1, Lot l2) {
            int result = l1.date.compareTo(l2.date);
            if (result == 0) {
                result = l1.stock.getTicker().compareTo(l2.stock.getTicker());
            }
            if (result == 0) {
                result = l1.stock.hashCode() - l2.stock.hashCode();
            }
            return result;
        }

    }

    private static class Count {

        private int count;

        public Count() {
        }

        int minus(int delta) {
            count -= delta;
            return count;
        }

        int plus(int delta) {
            count += delta;
            return count;
        }

        int getCount() {
            return count;
        }

    }
}
