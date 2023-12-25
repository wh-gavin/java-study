package org.tacos.jpa;

import org.springframework.data.repository.CrudRepository;
import org.tacos.domain.Ingredient;

public interface IngredientRepository 
         extends CrudRepository<Ingredient, String> {

}
