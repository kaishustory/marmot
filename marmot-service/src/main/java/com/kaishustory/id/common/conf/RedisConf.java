/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.kaishustory.id.common.conf;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Redis配置
 */
@Configuration
public class RedisConf {

    @Bean(name = "redisTemplate")
    public StringRedisTemplate redisWxTemplate(
            @Value("${redis.comm.host}") String hostName,
            @Value("${redis.comm.port}") int port,
            @Value("${redis.comm.password}") String password,
            @Value("${redis.comm.database}") int database,
            @Value("${redis.comm.pool.max-idle}") int maxIdle,
            @Value("${redis.comm.pool.min-idle}") int minIdle,
            @Value("${redis.comm.pool.max-active}") int maxActive,
            @Value("${redis.comm.pool.max-wait}") long maxWaitMillis) {
        StringRedisTemplate temple = new StringRedisTemplate();
        temple.setConnectionFactory(connectionFactory(hostName, port, password,database,
                maxIdle, minIdle, maxActive, maxWaitMillis));

        return temple;
    }

    private RedisConnectionFactory connectionFactory(String hostName, int port,
                                                     String password, int database, int maxIdle, int minIdle, int maxActive,
                                                     long maxWaitMillis) {
        JedisConnectionFactory jedis = new JedisConnectionFactory();
        jedis.setDatabase(database);
        jedis.setHostName(hostName);
        jedis.setPort(port);
        if (!StringUtils.isEmpty(password)) {
            jedis.setPassword(password);
        }
        jedis.setPoolConfig(poolConfig(maxIdle, minIdle, maxActive, maxWaitMillis, true));
        jedis.afterPropertiesSet();
        return jedis;
    }

    private JedisPoolConfig poolConfig(int maxIdle, int minIdle, int maxActive,
                                       long maxWaitMillis, boolean testOnBorrow) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setTestOnBorrow(testOnBorrow);
        return poolConfig;
    }
}
