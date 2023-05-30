package redis.spring.events.send;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface CoffeeSource {
	
	String OUTPUT="coffeeSend";
	
	@Output(CoffeeSource.OUTPUT)
	MessageChannel output();
	
}
