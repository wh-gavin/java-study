package springcloud.configclient;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;

/**
 * Hello world!
 *
 */
@SpringBootApplication(exclude= {DataSourceAutoConfiguration.class,DataSourceTransactionManagerAutoConfiguration.class})
public class ConfigClientApplication {
	public static void main(String[] args) {
		System.currentTimeMillis();
		Date sd = new Date();
		SpringApplication.run(ConfigClientApplication.class, args);
	}
}
