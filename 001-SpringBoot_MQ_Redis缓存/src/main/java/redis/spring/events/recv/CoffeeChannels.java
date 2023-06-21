package redis.spring.events.recv;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface CoffeeChannels {
	
    @Input("inboundOrgChanges")
    SubscribableChannel coffee();
    
}
