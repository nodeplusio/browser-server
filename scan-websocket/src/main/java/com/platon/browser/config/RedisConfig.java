package com.platon.browser.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class RedisConfig {

    @Autowired
    private RedissonConfig redissonConfig;

    @Bean
    public RedissonClient redissonClient() {
        // 配置
        Config config = new Config();
        // 地址 & 密码
        SingleServerConfig singleServerConfig = config.useSingleServer();
        singleServerConfig.setAddress(redissonConfig.getAddress()).setPassword(redissonConfig.getPassword());
        // 创建 RedissonClient 对象
        return Redisson.create(config);
    }

}
