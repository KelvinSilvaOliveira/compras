package com.listadecompras.compras.core.domain.factory;

import com.listadecompras.compras.core.domain.Compra;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map;

public abstract class CompraFactory {

    protected Map<String, Integer> items;
    protected String comment;

    protected Map<String, SimpleEntry<Double, Double>> addendum;

    protected Compra compra;

    public Compra getCompraMarket(Map<String, Integer> items, String comment) {
        this.items = items;
        this.comment = comment;
        return create();
    }

    public Compra getCompraMarketRenewed(Compra compra, Map<String, SimpleEntry<Double, Double>> addendum) {
        this.compra = compra;
        this.addendum = addendum;
        return create();
    }

    public abstract Compra create();

}
