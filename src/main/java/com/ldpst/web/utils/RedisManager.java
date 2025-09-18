package com.ldpst.web.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ldpst.web.server.ShootController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.math.BigDecimal;
import java.util.Map;

public class RedisManager {
    private static final Logger logger = new Logger("logs/logs.txt");
    private static final String REDIS_HOST = "redis-java";
    private static final int REDIS_PORT = 6379;
    private static final PSQLManager psql = new PSQLManager();

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static JedisPool jedisPool;

    public RedisManager() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);

        jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT);
    }

    public String getShoots(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            String resp = jedis.get(key);

            if (resp != null) return resp;

            resp = psql.getShoots();
            saveString(key, resp);

            return resp;
        }
    }

    public void invalidate(String key) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(key);
        }
    }

    private void saveString(String key, String value) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.set(key, value);
        }
    }

    public Map<String, Object> getShoot(String key, Map<String, BigDecimal> map) {
        try (Jedis jedis = jedisPool.getResource()) {
            String resp = jedis.get(key);
            if (resp != null) {
                try {
                    Map<String, Object> res = objectMapper.readValue(
                            resp,
                            new TypeReference<Map<String, Object>>() {}
                    );
                    return res;
                } catch (JsonProcessingException e) {
                    //
                }
            }

            Map<String, Object> res = ShootController.buildShootMap(map);
            saveMap(key, res);
            return res;
        }
    }

    public void saveMap(String key, Map<String, Object> map) {
        try (Jedis jedis = jedisPool.getResource()) {
            String json = objectMapper.writeValueAsString(map);
            jedis.set(key, json);
        } catch (JsonProcessingException e) {
            //
        }
    }
}
