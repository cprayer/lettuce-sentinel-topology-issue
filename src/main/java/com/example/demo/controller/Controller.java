package com.example.demo.controller;

import io.lettuce.core.masterreplica.StatefulRedisMasterReplicaConnection;
import io.lettuce.core.sentinel.api.StatefulRedisSentinelConnection;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.embedded.Redis;
import redis.embedded.RedisCluster;

@RestController
public class Controller {

    private final StatefulRedisMasterReplicaConnection<byte[], byte[]> masterReplicaConnection;
    private final StatefulRedisSentinelConnection<String, String> sentinelConnection;
    private final RedisCluster redisCluster;

    public Controller(StatefulRedisMasterReplicaConnection<byte[], byte[]> masterReplicaConnection, StatefulRedisSentinelConnection<String, String> sentinelConnection, RedisCluster redisCluster) {
        this.masterReplicaConnection = masterReplicaConnection;
        this.sentinelConnection = sentinelConnection;
        this.redisCluster = redisCluster;
    }

    @GetMapping
    public String entry() {
        return masterReplicaConnection.sync().set("hello".getBytes(), "world".getBytes());
    }

    @GetMapping("/stop")
    public void stop() {
        for (Redis redisSentinel : redisCluster.sentinels()) {
            for (int port : redisSentinel.ports()) {
                if (port == 26379) {
                    redisSentinel.stop();
                    return;
                }
            }
        }
    }

    @GetMapping("/start")
    public void start() {
        for (Redis redisSentinel : redisCluster.sentinels()) {
            for (int port : redisSentinel.ports()) {
                if (port == 26379) {
                    redisSentinel.start();
                    return;
                }
            }
        }
    }

    @GetMapping("/failover")
    public void failover() {
        sentinelConnection.sync().failover("mymaster");
    }
}
