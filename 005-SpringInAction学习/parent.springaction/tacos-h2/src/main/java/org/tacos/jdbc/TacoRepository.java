package org.tacos.jdbc;

import org.tacos.domain.Taco;

public interface TacoRepository  {

  Taco save(Taco design);
  
}
