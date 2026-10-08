package seedu.duke;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

class OrderListTest {
    @Test
    public void getAll_twoOrders_returnsOldestFirst() {
        OrderList orders = new OrderList();
        orders.create("Jonas Low", List.of());
        orders.create("Mei", List.of());

        assertEquals("Jonas Low", orders.getAll().get(0).getCustomer());
        assertEquals("Mei", orders.getAll().get(1).getCustomer());
    }

    @Test
    public void getAll_returnedList_cannotBeModified() {
        OrderList orders = new OrderList();
        orders.create("Jonas Low", List.of());

        assertThrows(UnsupportedOperationException.class, () -> orders.getAll().clear());
        assertEquals(1, orders.size());
    }

    @Test
    public void getConfirmedSales_excludesCancelledOrders() throws SystemException {
        OrderList orders = new OrderList();
        Order confirmed = orders.create("Jonas Low", List.of());
        Order cancelled = orders.create("Mei", List.of());
        cancelled.cancel();

        assertEquals(List.of(confirmed), orders.getConfirmedSales());
    }
}
