package redis.spring.model;

import javax.persistence.Id;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@RedisHash(value = "shop-coffee", timeToLive=60)
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoffeeCache {
	@Id
	private Long id;
	@Indexed
	private String name;
	private Long price;
}
