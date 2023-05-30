package redis.spring.events.send;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import redis.spring.model.Coffee;

//@Component
public class SimpleSourceBean {
	private Source source;

	private static final Logger logger = LoggerFactory.getLogger(SimpleSourceBean.class);

	@Autowired
	public SimpleSourceBean(Source source) {
		this.source = source;
	}

	public void publishCoffeeChange(Coffee coffee) {
//		Coffee coffee = new Coffee();
//		coffee.setName("xzw publish kafka");
//		coffee.setPrice(1000L);
//		coffee.setCreateTime(new java.sql.Date((new Date()).getTime()));
		logger.info("====Sending Kafka message for Coffee: {}", coffee.toString());
		
		source.output().send(MessageBuilder.withPayload(coffee).build());
	}
}
