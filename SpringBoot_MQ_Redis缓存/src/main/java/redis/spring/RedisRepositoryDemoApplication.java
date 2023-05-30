package redis.spring;

import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import lombok.extern.slf4j.Slf4j;
import redis.spring.events.send.CoffeeSendService;
import redis.spring.model.Coffee;
import redis.spring.repository.CoffeeRepository;
import redis.spring.service.CoffeeService;

@SpringBootApplication
@EnableJpaRepositories
@EnableRedisRepositories
@Slf4j
@EnableBinding(Source.class)
public class RedisRepositoryDemoApplication implements CommandLineRunner {
	@Autowired
	private CoffeeRepository coffeeRepository;
	@Autowired
	private CoffeeService coffeeService;
	//@Autowired
	//private SimpleSourceBean simpleSourceBean;
	@Autowired
	private CoffeeSendService coffeeSendService;
	
	
	public static void main(String[] args) {
		SpringApplication.run(RedisRepositoryDemoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Optional<Coffee> coffeeBean = coffeeService.findSimpleCoffeeByName("latte");
		log.info("First Fetch Coffee: {} ", coffeeBean.get());
		coffeeBean = coffeeService.findSimpleCoffeeByName("latte");
		log.info("Second Fetch Coffee: {} ", coffeeBean.get());
		
		log.info("add coffee");
		Coffee coffee = new Coffee();
		coffee.setName("xzw");
		coffee.setPrice(1000L);
		coffee.setCreateTime(new java.sql.Date((new Date()).getTime()));
		
		coffeeRepository.save(coffee);
		coffeeBean = coffeeService.findSimpleCoffeeByName("xzw");
		log.info("add Fetch Coffee: {} ", coffeeBean.get());
		
		coffeeSendService.sendMessage(coffee);
	}
}
