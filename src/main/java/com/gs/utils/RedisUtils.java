package com.gs.utils;

import com.alibaba.fastjson.JSONObject;
import com.gs.bean.News;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;

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


    public static void insertNews(News news){
        String newsStr = JSONObject.toJSONString(news);
        Jedis jedis = getJedis();
        jedis.hset("news",String.valueOf(news.getId()),newsStr);
        close(jedis);
    }

    public static News getNewsByID(String id){
        Jedis jedis = getJedis();
        String newsStr = jedis.hget("news", id);
        News news = JSONObject.parseObject(newsStr, News.class);
        close(jedis);
        return news;
    }

    public static void deleteByID(String id){
        Jedis jedis = getJedis();
        jedis.hdel("news",id);
        close(jedis);
    }

    public static void updateNews(News news){
        Jedis jedis = getJedis();
        jedis.hset("news",String.valueOf(news.getId()),JSONObject.toJSONString(news));
        close(jedis);
    }



    //每次拿取jedis实例使用完后，需要返回到资源池
    public static void close(Jedis jedis){
        if(jedisPool==null){
            initJedisPool();
        }
        jedisPool.returnResource(jedis);
    }

    public static void main(String[] args) {
        News news = new News();
        news.setPubTime(new Date());
        news.setId(1);
        news.setAuthor("jyw");
        news.setContent("first news");
        news.setTitle("abc");
        news.setAbstracts("摘要");

        insertNews(news);

        News id = getNewsByID("1");


        news.setAuthor("tl");
        updateNews(news);

        deleteByID("1");
    }

}
