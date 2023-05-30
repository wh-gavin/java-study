package redis.spring.events.send;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

import redis.spring.model.Coffee;

@EnableBinding(CoffeeSource.class)
public class CoffeeSendService {
	private static final Logger logger = LoggerFactory.getLogger(CoffeeSendService.class);
	@Autowired
	private CoffeeSource coffeeSource;

	public void sendMessage(Coffee coffee) {
		logger.info("====Sending Kafka message for Coffee: {}", coffee.toString());
		coffeeSource.output().send(MessageBuilder.withPayload(coffee).build());
	}
}
