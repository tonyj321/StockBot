package com.aruninc.stockbot;

/**
 *
 * @author tonyj
 */
class StockValueNotAvailable extends RuntimeException {

    public StockValueNotAvailable(String message) {
        super(message);
    }
    
}
