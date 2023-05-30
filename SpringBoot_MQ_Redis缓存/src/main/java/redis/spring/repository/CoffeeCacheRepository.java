package redis.spring.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import redis.spring.model.CoffeeCache;


public interface CoffeeCacheRepository extends CrudRepository<CoffeeCache, Long>{
	public Optional<CoffeeCache> findOneByName(String name);
}
