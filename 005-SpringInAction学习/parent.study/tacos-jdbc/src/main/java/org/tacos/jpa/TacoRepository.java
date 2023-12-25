package org.tacos.jpa;

import org.springframework.data.repository.CrudRepository;
import org.tacos.domain.Taco;

public interface TacoRepository 
         extends CrudRepository<Taco, Long> {

}
