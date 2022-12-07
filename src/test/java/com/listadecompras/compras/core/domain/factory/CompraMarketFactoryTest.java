package com.listadecompras.compras.core.domain.factory;

import com.listadecompras.compras.core.domain.Compra;
import com.listadecompras.compras.core.domain.factory.impl.CompraMarket;
import com.listadecompras.compras.core.domain.market.Item;
import com.listadecompras.compras.core.domain.market.Product;
import com.listadecompras.compras.core.domain.market.Purchase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.*;

public class CompraMarketFactoryTest {

    @Test
    public void getCompraMarket_success() {
        // given
        CompraFactory compraFactory = new CompraMarket();
        Map<String, Integer> items = new HashMap<>();
        items.put("teste", 1);

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarket(items, "teste");

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(
                List.of(new Item(new Product("teste", null, null), 1, false)),
                purchase.items()
        );
        Assertions.assertEquals("teste", purchase.comments());
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(false, purchase.isDone());
    }

    @Test
    public void getCompraMarket_without_items() {
        // given
        CompraFactory compraFactory = new CompraMarket();

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarket(null, "teste");

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void getCompraMarket_with_items_empty() {
        // given
        CompraFactory compraFactory = new CompraMarket();

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarket(new HashMap<>(), "teste");

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void getCompraMarket_without_comment() {
        // given
        CompraFactory compraFactory = new CompraMarket();
        Map<String, Integer> items = new HashMap<>();
        items.put("teste", 1);

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarket(items, null);

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(
                List.of(new Item(new Product("teste", null, null), 1, false)),
                purchase.items()
        );
        Assertions.assertEquals(null, purchase.comments());
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(false, purchase.isDone());
    }

    @Test
    public void getCompraMarketRenewed_success() {
        // given
        CompraFactory compraFactory = new CompraMarket();

        List<Item> items = new ArrayList<>();
        items.add(new Item(new Product("teste", null, null), 1, false));
        items.add(new Item(new Product("outro", null, null), 1, false));
        Compra compra = new Purchase(LocalDate.now(), items, "teste", false);
        Map<String, java.util.AbstractMap.SimpleEntry<Double, Double>> addendum = new HashMap<>();
        addendum.put("teste", new AbstractMap.SimpleEntry<>(1., 1.));
        addendum.put("outro", new AbstractMap.SimpleEntry<>(null, null));

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarketRenewed(compra, addendum);

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(
                List.of(
                        new Item(new Product("teste", 1., 1.), 1, true),
                        new Item(new Product("outro", null, null), 1, false)
                ),
                purchase.items()
        );
        Assertions.assertEquals("teste", purchase.comments());
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(true, purchase.isDone());
    }

    @Test
    public void getCompraMarketRenewed_without_compra() {
        // given
        CompraFactory compraFactory = new CompraMarket();

        Map<String, java.util.AbstractMap.SimpleEntry<Double, Double>> addendum = new HashMap<>();
        addendum.put("teste", new AbstractMap.SimpleEntry<>(1., 1.));

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarketRenewed(null, addendum);

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void getCompraMarketRenewed_without_addendum() {
        // given
        CompraFactory compraFactory = new CompraMarket();

        Compra compra = new Purchase(LocalDate.now(), List.of(), "teste", false);

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarketRenewed(compra, null);

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(List.of(), purchase.items());
        Assertions.assertEquals("teste", purchase.comments());
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(false, purchase.isDone());
    }

    @Test
    public void getCompraMarketRenewed_with_addendum_empty() {
        // given
        CompraFactory compraFactory = new CompraMarket();

        Compra compra = new Purchase(LocalDate.now(), List.of(), "teste", false);

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarketRenewed(compra, new HashMap<>());

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(List.of(), purchase.items());
        Assertions.assertEquals("teste", purchase.comments());
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(false, purchase.isDone());
    }

    @Test
    public void getCompraMarketRenewed_without_compra_and_addendum() {
        // given
        CompraFactory compraFactory = new CompraMarket();

        // when
        Purchase purchase = (Purchase) compraFactory.getCompraMarketRenewed(null, null);

        // then
        Assertions.assertNull(purchase);
    }
}
