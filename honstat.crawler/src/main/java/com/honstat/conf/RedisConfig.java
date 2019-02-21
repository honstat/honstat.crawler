package com.honstat.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;


/**
 * Redis缓存配置类
 *
 * @author szekinwin
 */

@Configuration
public class RedisConfig {
    @Value("${redis.ip}")
    private String redisIp;
    @Value("${redis.port}")
    private Integer redisPort;
    @Value("${redis.password}")
    private String redisPassword;
    @Value("${redis.index}")
    private Integer redisIndex;

    public RedisConfig() {
    }

    @Bean
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(200);
        jedisPoolConfig.setMinIdle(5);
        jedisPoolConfig.setMaxTotal(200);
        jedisPoolConfig.setTestOnBorrow(true);
        jedisPoolConfig.setTestWhileIdle(true);
        jedisPoolConfig.setTestOnReturn(true);
        jedisPoolConfig.setMaxWaitMillis(5000L);
        return jedisPoolConfig;
    }

    @Bean
    public JedisConnectionFactory getJedisConnectionFactory(JedisPoolConfig jedisPoolConfig) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
        jedisConnectionFactory.setHostName(this.redisIp);
        jedisConnectionFactory.setPort(this.redisPort.intValue());
        jedisConnectionFactory.setDatabase(this.redisIndex.intValue());
        jedisConnectionFactory.setPassword(this.redisPassword);
        jedisConnectionFactory.setTimeout(10000);
        jedisConnectionFactory.setUsePool(true);
        return jedisConnectionFactory;
    }

    @Bean(
            name = {"redisTemplate"}
    )
    public RedisTemplate getRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());
        redisTemplate.setHashValueSerializer(new JdkSerializationRedisSerializer());
        return redisTemplate;
    }
}



