package redis.spring.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import redis.spring.model.Coffee;


public interface CoffeeRepository extends JpaRepository<Coffee, Long>{
	public Optional<Coffee> findOneByNameIgnoreCase(String name);
}
