package org.tacos.jpa;

import org.springframework.data.repository.CrudRepository;
import org.tacos.domain.Order;

public interface OrderRepository 
         extends CrudRepository<Order, Long> {

}
