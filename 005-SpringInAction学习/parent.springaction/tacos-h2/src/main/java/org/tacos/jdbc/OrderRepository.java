package org.tacos.jdbc;

import org.tacos.domain.Order;

public interface OrderRepository {

  Order save(Order order);
  
}
