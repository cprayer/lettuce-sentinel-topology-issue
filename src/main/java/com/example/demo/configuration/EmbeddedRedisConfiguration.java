package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import redis.embedded.RedisCluster;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisExecProvider;
import redis.embedded.RedisSentinelBuilder;
import redis.embedded.RedisServerBuilder;
import redis.embedded.util.Architecture;
import redis.embedded.util.OS;

import java.util.List;

@Configuration
public class EmbeddedRedisConfiguration {

    @Bean
    public RedisCluster redisCluster() {
        RedisExecProvider execProvider = RedisExecProvider.defaultProvider();
        execProvider.override(OS.UNIX, Architecture.x86_64, "binary/redis-server-6.2.6");
        RedisCluster redisCluster = RedisCluster.builder().ephemeral()
                .withSentinelBuilder(new RedisSentinelBuilder()
                        .quorumSize(2)
                        .redisExecProvider(execProvider)
                )
                .withServerBuilder(new RedisServerBuilder()
                        .redisExecProvider(execProvider)
                )
                .sentinelPorts(List.of(26379, 26380, 26381))
                .serverPorts(List.of(16379, 16380, 16381))
                .replicationGroup("mymaster", 2)
                .build();

        redisCluster.start();
        return redisCluster;
    }
}
