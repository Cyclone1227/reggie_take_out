package com.zwt;

import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;


public class jedisTest {

    @Test
    public void testRedis(){
        //建立连接
        Jedis jedis = new Jedis("localhost",6379);
        //执行具体操作
        jedis.set("username","zhou");
        String username = jedis.get("username");
        System.out.println(username);

        jedis.hset("myhash","name","230");
        String hget = jedis.hget("myhash", "name");
        System.out.println(hget);
        //关闭连接
        jedis.close();

    }
}

