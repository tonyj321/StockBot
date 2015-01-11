package com.aruninc.stockbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tonyj
 */
public class StockBot {

    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        StockBot stockBot = new StockBot();
        String home = System.getProperty("user.home");
        stockBot.readZip(Paths.get(home, "NetBeansProjects/StockBot/StockBot/NASDAQ_2014.zip"));
        System.out.println("Nasdaq 2014 archived");
        stockBot.readZip(Paths.get(home, "NetBeansProjects/StockBot/StockBot/NASDAQ_2013.zip"));
        System.out.println("Nasdaq 2013 archived");
        stockBot.readZip(Paths.get(home, "NetBeansProjects/StockBot/StockBot/NASDAQ_2012.zip"));
        System.out.println("Nasdaq 2012 archived");
        stockBot.readZip(Paths.get(home, "NetBeansProjects/StockBot/StockBot/NASDAQ_2011.zip"));
        System.out.println("Nasdaq 2011 archived");
        stockBot.readZip(Paths.get(home, "NetBeansProjects/StockBot/StockBot/NASDAQ_2010.zip"));
        System.out.println("Nasdaq 2010 archived");
        stockBot.readZip(Paths.get(home, "NetBeansProjects/StockBot/StockBot/NASDAQ_2009.zip"));
        System.out.println("Nasdaq 2009 archived");
        long read = System.currentTimeMillis();
        System.out.printf("Read took %,dms\n",read-start);

        Stock stock = stockBot.stockMap.get("IRBT");
        final StockDate date = StockDate.parse("2014-07-30");
        System.out.println(stock.get(date));
        
        double previousValue = 0;
        double buyPrice = -1;
        double percentProfit = 0;
        double totalProfit = 0;
        double totalPercentProfit = 0;
        
        int nStocks = 0;
        for (String ticker : stockBot.stockMap.keySet()) {
            stock = stockBot.stockMap.get(ticker);
            try {
                double subTotalProfit = 0;
                Indicator sma = new SimpleMovingAverage(stock.closingPrices(), 30);
                //System.out.printf("sma=%5g\n", sma.valueAt(date));

                Indicator ema = new ExponentialMovingAverage(stock.closingPrices(), 30);
                //System.out.printf("ema=%5g\n", ema.valueAt(date));

                Indicator macd = new MovingAverageConvergenceDivergenceOscillator(stock.closingPrices(), 12, 26, 9);
                //System.out.printf("macd=%5g\n", macd.valueAt(date));

                
                for (StockDate day : StockDate.range(StockDate.parse("2014-01-29"), StockDate.parse("2014-09-29"))) {

                    final double currentValue = macd.valueAt(day);
                    //System.out.printf("%s: macd=%5g\n", day, currentValue);
                    if (Math.signum(previousValue) != Math.signum(currentValue) && previousValue != 0) {
                        if (currentValue > 0) {
                            //System.out.printf("%s: Buy!\n", day);
                            buyPrice = stock.closingPrices().valueAt(day);
                        } else {
                            if (buyPrice != -1) {
                                double profit = stock.closingPrices().valueAt(day) - buyPrice;
                                //System.out.printf("%s: Sell! Profit = %5g\n", day, profit);
                                subTotalProfit += profit;

                            }
                        }
                    }
                    previousValue = currentValue;

                }
                System.out.printf("Sub-Total Profit = %5g\n", subTotalProfit);
                totalProfit += subTotalProfit;
                if (nStocks++>10) break;
                
            } catch (StockValueNotAvailable x) {
                System.out.println("Skipping "+ticker+" because: "+x.getMessage());
            }
            
        }
        System.out.printf("Total Profit = %5g\n", totalProfit);
        long stop = System.currentTimeMillis();
        System.out.printf("Analyze took %,dms\n",stop-read);
    }

    private final Map<String, Stock> stockMap = new HashMap<>();

    private void readZip(Path zipPath) throws IOException {
        FileSystem fs = FileSystems.newFileSystem(zipPath, null);
        for (Path dir : fs.getRootDirectories()) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
                for (Path file : stream) {
                    readFile(file);
                }
            }
        }
    }

    private void readFile(Path file) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            for (;;) {
                String line = reader.readLine();
                if (line == null) {
                    break;
                }
                String[] tokens = line.split(",");
                String ticker = tokens[0];
                StockDate date = StockDate.put(tokens[1], DateTimeFormatter.BASIC_ISO_DATE);
                double open = Double.parseDouble(tokens[2]);
                double high = Double.parseDouble(tokens[3]);
                double low = Double.parseDouble(tokens[4]);
                double close = Double.parseDouble(tokens[5]);
                long volume = Long.parseLong(tokens[6]);
                Stock stock = stockMap.get(ticker);
                if (stock == null) {
                    stock = new Stock(ticker);
                    stockMap.put(ticker, stock);
                }
                stock.addData(date, open, high, low, close, volume);
            }
        }
    }
}
