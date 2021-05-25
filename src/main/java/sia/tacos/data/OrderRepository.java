package sia.tacos.data;

import sia.tacos.Order;

public interface OrderRepository {
    Order save(Order order);
}
