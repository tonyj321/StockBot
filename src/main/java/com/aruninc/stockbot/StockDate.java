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

    StockDate(LocalDate date) {
        this.date = date;
        dates.add(this);
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

    static StockDate parse(String string, DateTimeFormatter dateFormat) {
        return new StockDate(LocalDate.parse(string, dateFormat));
    }

    static StockDate parse(String string) {
        return new StockDate(LocalDate.parse(string));
    }

    @Override
    public int compareTo(StockDate other) {
        return date.compareTo(other.date);
    }
}
