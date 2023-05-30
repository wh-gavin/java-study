package redis.spring.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import redis.spring.model.Coffee;
import redis.spring.model.CoffeeCache;
import redis.spring.repository.CoffeeCacheRepository;
import redis.spring.repository.CoffeeRepository;

@Service
@Slf4j
public class CoffeeService {
	@Autowired
	private CoffeeRepository coffeeRepository;
	@Autowired
	private CoffeeCacheRepository coffeeCacheRepository;
	
	public Optional<Coffee> findSimpleCoffeeByName(String name) {
		Optional<CoffeeCache> coffeeCache = coffeeCacheRepository.findOneByName(name);
		if (coffeeCache.isPresent()) {
			CoffeeCache coffeeBean = coffeeCache.get();
			Coffee result = Coffee.builder().id(coffeeBean.getId())
											.name(coffeeBean.getName())
											.price(coffeeBean.getPrice())
											.build();
			log.info("Get Coffee From Cache: {}", result);
			return Optional.of(result);
		}
		Optional<Coffee> coffeeDB = coffeeRepository.findOneByNameIgnoreCase(name);
		coffeeDB.ifPresent(c -> {
			CoffeeCache coffeeCacheNew = CoffeeCache.builder()
					                                .id(c.getId())
					                                .name(c.getName())
					                                .price(c.getPrice())
					                                .build();
			coffeeCacheRepository.save(coffeeCacheNew);
			log.info("Save Coffee In Cache: {}", coffeeCacheNew);
		});
		return coffeeDB;
			
	}
	
}
