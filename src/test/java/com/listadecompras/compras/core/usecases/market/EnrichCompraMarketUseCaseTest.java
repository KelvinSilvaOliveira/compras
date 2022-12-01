package com.listadecompras.compras.core.usecases.market;

import com.listadecompras.compras.core.dataprovider.CompraDataProvider;
import com.listadecompras.compras.core.domain.Compra;
import com.listadecompras.compras.core.domain.factory.impl.CompraMarket;
import com.listadecompras.compras.core.domain.market.Item;
import com.listadecompras.compras.core.domain.market.Product;
import com.listadecompras.compras.core.domain.market.Purchase;
import com.listadecompras.compras.core.usecases.market.impl.EnrichCompraMarketUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnrichCompraMarketUseCaseTest {

    private CompraDataProvider compraDataProvider = newCompraDataProvider();

    @Test
    public void enrichCompraList_success() {
        // given
        EnrichCompraListUseCase enrichCompraListUseCase = new EnrichCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);

        Product product = new Product("teste", null, null);
        Item item = new Item(product, 1, false);
        Compra compra = new Purchase(LocalDate.now(), List.of(item), "testando", false);
        Map<String, AbstractMap.SimpleEntry<Double, Double>> addendum = new HashMap<>();
        addendum.put("teste", new AbstractMap.SimpleEntry<>(1.,1.));

        // when
        enrichCompraListUseCase.enrichCompraList(compra, addendum);
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(
                List.of(new Item(new Product("teste", 1., 1.), 1, true)),
                purchase.items()
        );
        Assertions.assertEquals("testando", purchase.comments());
        Assertions.assertEquals(true, purchase.isDone());
    }

    @Test
    public void enrichCompraList_without_compra() {
        // given
        EnrichCompraListUseCase enrichCompraListUseCase = new EnrichCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);

        Map<String, AbstractMap.SimpleEntry<Double, Double>> addendum = new HashMap<>();
        addendum.put("teste", new AbstractMap.SimpleEntry<>(1.,1.));

        // when
        enrichCompraListUseCase.enrichCompraList(null, addendum);
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void enrichCompraList_with_items_compra_empty() {
        // given
        EnrichCompraListUseCase enrichCompraListUseCase = new EnrichCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);

        Compra compra = new Purchase(LocalDate.now(), List.of(), "testando", false);
        Map<String, AbstractMap.SimpleEntry<Double, Double>> addendum = new HashMap<>();
        addendum.put("teste", new AbstractMap.SimpleEntry<>(1.,1.));

        // when
        enrichCompraListUseCase.enrichCompraList(compra, addendum);
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void enrichCompraList_with_items_compra_done() {
        // given
        EnrichCompraListUseCase enrichCompraListUseCase = new EnrichCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);

        Product product = new Product("teste", 1., 1.);
        Item item = new Item(product, 1, true);
        Compra compra = new Purchase(LocalDate.now(), List.of(item), "testando", true);
        Map<String, AbstractMap.SimpleEntry<Double, Double>> addendum = new HashMap<>();
        addendum.put("teste", new AbstractMap.SimpleEntry<>(2.,2.));

        // when
        enrichCompraListUseCase.enrichCompraList(compra, addendum);
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(
                List.of(new Item(new Product("teste", 2., 2.), 1, true)),
                purchase.items()
        );
        Assertions.assertEquals("testando", purchase.comments());
        Assertions.assertEquals(true, purchase.isDone());
    }

    @Test
    public void enrichCompraList_without_addendum() {
        // given
        EnrichCompraListUseCase enrichCompraListUseCase = new EnrichCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);

        Product product = new Product("teste", null, null);
        Item item = new Item(product, 1, false);
        Compra compra = new Purchase(LocalDate.now(), List.of(item), "testando", false);

        // when
        enrichCompraListUseCase.enrichCompraList(compra, null);
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void enrichCompraList_with_addendum_empty() {
        // given
        EnrichCompraListUseCase enrichCompraListUseCase = new EnrichCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);

        Product product = new Product("teste", null, null);
        Item item = new Item(product, 1, false);
        Compra compra = new Purchase(LocalDate.now(), List.of(item), "testando", false);
        Map<String, AbstractMap.SimpleEntry<Double, Double>> addendum = new HashMap<>();

        // when
        enrichCompraListUseCase.enrichCompraList(compra, addendum);
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNull(purchase);
    }

    private CompraDataProvider newCompraDataProvider() {
        return new CompraDataProvider() {

            private Compra compra;

            @Override
            public Compra registerCompra(Compra compra) {
                return this.compra = compra;
            }

            @Override
            public Compra updateCompra(Compra compra) {
                return this.compra = compra;
            }

            @Override
            public Compra searchCompra(LocalDate dateReference) {
                Purchase purchase = (Purchase) this.compra;
                if(purchase == null) return null;
                return purchase.dateReference().getMonth().equals(dateReference.getMonth()) ?
                        purchase : null;
            }

            @Override
            public List<Compra> searchCompras() {
                return List.of(this.compra);
            }
        };
    }
}
