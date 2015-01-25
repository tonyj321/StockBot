package com.aruninc.stockbot;

import java.util.ArrayList;

/**
 *
 * @author tonyj
 */
class Stock {

    private final String ticker;
    private final ArrayList<OpenHighLowCloseVolume> data = new ArrayList<>();

    Stock(String ticker) {
        this.ticker = ticker;
    }

    void addData(StockDate date, double open, double high, double low, double close, long volume) {
        final int uniqueId = date.getUniqueId();
        while (uniqueId > data.size()) {
            data.add(null);
        }
        if (uniqueId == data.size()) {
            data.add(new OpenHighLowCloseVolume(open, high, low, close, volume));
        } else {
            data.set(uniqueId, new OpenHighLowCloseVolume(open, high, low, close, volume));
        }
    }

    @Override
    public String toString() {
        return "Stock{" + "ticker=" + ticker + ", data=" + data + '}';
    }

    OpenHighLowCloseVolume get(StockDate date) {
        final int uniqueId = date.getUniqueId();
        if (uniqueId < data.size()) {
            final OpenHighLowCloseVolume datum = data.get(uniqueId);
            if (datum != null) {
                return datum;
            }
        }
        throw new StockValueNotAvailable("Stock " + ticker + " not available for date " + date);
    }

    public String getTicker() {
        return ticker;
    }

    HistoricalValue closingPrices() {
        return new HistoricalValue() {

            @Override
            public double valueAt(StockDate date) {
                return get(date).getClose();
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
