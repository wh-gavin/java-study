package redis.spring.events.recv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

import redis.spring.model.Coffee;

@EnableBinding(CoffeeChannels.class)
public class CoffeeChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(CoffeeChangeHandler.class);

    @StreamListener("inboundOrgChanges")
    public void loggerSink(Coffee coffee) {
        logger.info("=== Received a message of type " + coffee.toString());
    }


}
