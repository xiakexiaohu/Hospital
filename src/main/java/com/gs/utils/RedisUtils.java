package com.gs.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisUtils {

    private static JedisPool jedisPool;

    //对多线程操作同步
    private static synchronized JedisPool  initJedisPool(){
        // 主机地址
        String host = "127.0.0.1";
        // 构建连接池配置信息
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 设置最大连接数
        jedisPoolConfig.setMaxActive(10);
        // 超时时间
        int timeout = 1000;
        // 构建连接池
        jedisPool = new JedisPool(jedisPoolConfig, host, 6379, timeout);
        return jedisPool;
    }

    //从连接池中直接拿jedis实例
    public static Jedis getJedis(){
        if(jedisPool==null){
            initJedisPool();
        }
        return jedisPool.getResource();
    }

    //每次拿取jedis实例使用完后，需要返回到资源池
    public static void close(Jedis jedis){
        if(jedisPool==null){
            initJedisPool();
        }
        jedisPool.returnResource(jedis);
    }

    public static void main(String[] args) {
        getJedis().lpush("a","c");
    }

}
