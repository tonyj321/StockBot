package com.aruninc.stockbot;

import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author tonyj
 */
class Stock {

    private final String ticker;
    private final Map<StockDate, OpenHighLowCloseVolume> data = new TreeMap<>();

    Stock(String ticker) {
        this.ticker = ticker;
    }

    void addData(StockDate date, double open, double high, double low, double close, long volume) {
        data.put(date, new OpenHighLowCloseVolume(open, high, low, close, volume));
    }

    @Override
    public String toString() {
        return "Stock{" + "ticker=" + ticker + ", data=" + data + '}';
    }

    OpenHighLowCloseVolume get(StockDate date) {
        return data.get(date);
    }
    
    HistoricalValue closingPrices() {
        return new HistoricalValue() {

            @Override
            public double valueAt(StockDate date) {
                return data.get(date).getClose();
            }
            
        };
    }
    
    public static class OpenHighLowCloseVolume {

        private final long volume;
        private final double close;
        private final double low;
        private final double high;
        private final double open;

        private OpenHighLowCloseVolume(double open, double high, double low, double close, long volume) {
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.volume = volume;
        }

        @Override
        public String toString() {
            return "OpenHighLowClose{" + "volume=" + volume + ", close=" + close + ", low=" + low + ", high=" + high + ", open=" + open + '}';
        }

        public long getVolume() {
            return volume;
        }

        public double getClose() {
            return close;
        }

        public double getLow() {
            return low;
        }

        public double getHigh() {
            return high;
        }

        public double getOpen() {
            return open;
        }
        
    }

}
