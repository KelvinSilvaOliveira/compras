package com.listadecompras.compras.core.usecases.market;

import com.listadecompras.compras.core.dataprovider.CompraDataProvider;
import com.listadecompras.compras.core.domain.Compra;
import com.listadecompras.compras.core.domain.factory.impl.CompraMarket;
import com.listadecompras.compras.core.domain.market.Item;
import com.listadecompras.compras.core.domain.market.Product;
import com.listadecompras.compras.core.domain.market.Purchase;
import com.listadecompras.compras.core.usecases.market.impl.MakeCompraMarketUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MakeCompraMarketUseCaseTest {

    private CompraDataProvider compraDataProvider = newCompraDataProvider();

    @Test
    public void makeCompraList_success() {
        // given
        MakeCompraListUseCase makeCompraListUseCase = new MakeCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);
        Map<String, Integer> items = new HashMap<>();
        items.put("teste", 1);

        // when
        makeCompraListUseCase.makeCompraList(items, "testando");
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(
                List.of(new Item(new Product("teste", null, null), 1, false)),
                purchase.items()
        );
        Assertions.assertEquals("testando", purchase.comments());
        Assertions.assertEquals(false, purchase.isDone());
    }

    @Test
    public void makeCompraList_with_items_empty() {
        // given
        MakeCompraListUseCase makeCompraListUseCase = new MakeCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);
        Map<String, Integer> items = new HashMap<>();

        // when
        makeCompraListUseCase.makeCompraList(items, "testando");
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void makeCompraList_without_items() {
        // given
        MakeCompraListUseCase makeCompraListUseCase = new MakeCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);

        // when
        makeCompraListUseCase.makeCompraList(null, "testando");
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNull(purchase);
    }

    @Test
    public void makeCompraList_without_comment() {
        // given
        MakeCompraListUseCase makeCompraListUseCase = new MakeCompraMarketUseCase(new CompraMarket(), this.compraDataProvider);
        Map<String, Integer> items = new HashMap<>();
        items.put("teste", 1);

        // when
        makeCompraListUseCase.makeCompraList(items, null);
        Purchase purchase = (Purchase) this.compraDataProvider.searchCompra(LocalDate.now());

        // then
        Assertions.assertNotNull(purchase);
        Assertions.assertEquals(LocalDate.now(), purchase.dateReference());
        Assertions.assertEquals(
                List.of(new Item(new Product("teste", null, null), 1, false)),
                purchase.items()
        );
        Assertions.assertEquals(null, purchase.comments());
        Assertions.assertEquals(false, purchase.isDone());
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
