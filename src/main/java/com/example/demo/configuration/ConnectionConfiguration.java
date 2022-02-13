package com.example.demo.configuration;

import io.lettuce.core.*;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.masterreplica.MasterReplica;
import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import java.time.Duration;

@Configuration
public class ConnectionConfiguration {

    @DependsOn("redisCluster")
    @Bean
    public StatefulRedisMasterReplicaConnection<byte[], byte[]> masterReplicaConnection() {
        RedisClient redisClient = RedisClient.create();
        redisClient.setOptions(ClientOptions.builder()
                .socketOptions(SocketOptions.builder()
                        .keepAlive(SocketOptions.KeepAliveOptions.builder()
                                .idle(Duration.ofSeconds(3))
                                .interval(Duration.ofSeconds(3))
                                .count(3)
                                .build())
                        .build())
                .build());

        StatefulRedisMasterReplicaConnection<byte[], byte[]> connection = MasterReplica.connect(redisClient, ByteArrayCodec.INSTANCE, RedisURI.builder()
                        .withSentinel("127.0.0.1", 26379)
                        .withSentinelMasterId("mymaster")
                .build());

        connection.setReadFrom(ReadFrom.MASTER);
        return connection;
    }

    @DependsOn("redisCluster")
    @Bean
    public StatefulRedisSentinelConnection<String, String> sentinelConnection() {
        RedisClient redisClient = RedisClient.create();
        redisClient.setOptions(ClientOptions.builder()
                .socketOptions(SocketOptions.builder()
                        .keepAlive(SocketOptions.KeepAliveOptions.builder()
                                .idle(Duration.ofSeconds(3))
                                .interval(Duration.ofSeconds(3))
                                .count(3)
                                .build())
                        .build())
                .build());

        return redisClient.connectSentinel(RedisURI.create("redis://127.0.0.1:26380"));
    }
}
