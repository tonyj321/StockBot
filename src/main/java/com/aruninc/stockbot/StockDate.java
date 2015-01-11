package com.aruninc.stockbot;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * Represents days that the stock marker is open.
 *
 * @author tonyj
 */
public class StockDate implements Comparable<StockDate> {

    private final LocalDate date;
    private static final TreeSet<StockDate> dates = new TreeSet<>();
    // Each item in dates is given a unique id (not necessary ordered)
    private int uniqueId;
    private static int ids = 1;

    private StockDate(LocalDate date) {
        this.date = date;
    }

    static StockDate first() {
        return dates.first();
    }

    static StockDate last() {
        return dates.last();
    }

    StockDate minus(int days) {
        Iterator<StockDate> descendingIterator = dates.headSet(this, true).descendingIterator();
        while (days-- > 0) {
            descendingIterator.next();
        }
        return descendingIterator.next();
    }

    StockDate plus(int days) {
        Iterator<StockDate> iterator = dates.tailSet(this, true).iterator();
        while (days-- > 0) {
            iterator.next();
        }
        return iterator.next();
    }

    static NavigableSet<StockDate> range(StockDate from, StockDate to) {
        return dates.subSet(from, true, to, true);
    }

    static StockDate put(String string, DateTimeFormatter dateFormat) {
        StockDate date = parse(string, dateFormat);
        if (date.uniqueId == 0) {
            dates.add(date);
            date.uniqueId = ids++;
        }
        return date;
    }

    static StockDate parse(String string, DateTimeFormatter dateFormat) {
        return unique(new StockDate(LocalDate.parse(string, dateFormat)));
    }

    static StockDate parse(String string) {
        return unique(new StockDate(LocalDate.parse(string)));
    }

    private static StockDate unique(StockDate date1) {
        if (dates.contains(date1)) {
            return dates.tailSet(date1, true).first();
        } else {
            return date1;
        }
    }

    @Override
    public int compareTo(StockDate other) {
        return date.compareTo(other.date);
    }

    public int getUniqueId() {
        return uniqueId;
    }

    @Override
    public String toString() {
        return date.toString();
    }

}
