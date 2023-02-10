package com.platon.browser.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix="spring.redis.redisson")
public class RedissonConfig {
	private String address;
	private String password;
}
