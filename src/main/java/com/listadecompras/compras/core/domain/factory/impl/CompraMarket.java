package com.listadecompras.compras.core.domain.factory.impl;

import com.listadecompras.compras.core.domain.Compra;
import com.listadecompras.compras.core.domain.factory.CompraFactory;
import com.listadecompras.compras.core.domain.market.Item;
import com.listadecompras.compras.core.domain.market.Product;
import com.listadecompras.compras.core.domain.market.Purchase;
import com.listadecompras.compras.core.domain.market.builder.concrete.ItemConcrete;
import com.listadecompras.compras.core.domain.market.builder.concrete.ProductConcrete;
import com.listadecompras.compras.core.domain.market.builder.concrete.PurchaseConcrete;
import com.listadecompras.compras.core.domain.market.builder.director.ItemDirector;
import com.listadecompras.compras.core.domain.market.builder.director.ProductDirector;
import com.listadecompras.compras.core.domain.market.builder.director.PurchaseDirector;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CompraMarket extends CompraFactory {

    private final PurchaseConcrete purchaseBuilder = new PurchaseConcrete();
    private final ItemConcrete itemBuilder = new ItemConcrete();
    private final ProductConcrete productBuilder = new ProductConcrete();

    @Override
    public Compra create() {
        PurchaseDirector purchaseDirector = new PurchaseDirector(this.purchaseBuilder);

        if(haveItems()) {
            purchaseDirector.constructPurchase(generateListItemsSimple(this.items), this.comment);
        } else if(haveAddendum() && haveCompra()) {
            Purchase purchase = (Purchase) compra;
            purchaseDirector.constructPurchase(generateListItemsComplete(purchase.items(), this.addendum), purchase.comments());
            purchaseDirector.constructPurchaseDone(this.purchaseBuilder.getPurchase());
        } else if(!haveItems() && !haveAddendum() && haveCompra()) {
            return this.compra;
        } else {
            return null;
        }

        return this.purchaseBuilder.getPurchase();
    }

    private boolean haveItems() {
        return this.items != null && !this.items.isEmpty();
    }

    private boolean haveAddendum() {
        return this.addendum != null && !this.addendum.isEmpty();
    }

    private boolean haveCompra() {
        return this.compra != null;
    }

    private List<Item> generateListItemsSimple(Map<String, Integer> items) {
        if(items == null) return null;
        return items.entrySet().stream()
                .map(item -> {
                    new ProductDirector(this.productBuilder).createProduct(item.getKey());
                    new ItemDirector(this.itemBuilder).constructItem(productBuilder.getProduct(), item.getValue());
                    return itemBuilder.getItem();
                })
                .toList();
    }

    private List<Item> generateListItemsComplete(List<Item> items, Map<String, AbstractMap.SimpleEntry<Double, Double>> addendum) {
        if(items == null) return null;

        ProductDirector productDirector = new ProductDirector(this.productBuilder);
        ItemDirector itemDirector = new ItemDirector(this.itemBuilder);

        return items.stream()
                .map(item -> {
                    AbstractMap.SimpleEntry<Double, Double> additionalInfo = Optional.ofNullable(addendum.get(item.product().name()))
                            .orElse(new AbstractMap.SimpleEntry<>(null, null));

                    if (isProductBought(addendum, item.product())) {
                        productDirector.createProductBought(item.product().name(), additionalInfo.getKey(), additionalInfo.getValue());
                        itemDirector.constructItemBought(productBuilder.getProduct(), item.quantity());
                    } else {
                        productDirector.createProduct(item.product().name());
                        itemDirector.constructItem(productBuilder.getProduct(), item.quantity());
                    }

                    return itemBuilder.getItem();
                })
                .toList();
    }

    private boolean isProductBought(Map<String, AbstractMap.SimpleEntry<Double, Double>> addendum, Product product) {
        boolean isBought = addendum.containsKey(product.name());

        if(isBought) isBought =
                addendum.get(product.name()).getKey() != null && addendum.get(product.name()).getValue() != null;

        return isBought;
    }

}
