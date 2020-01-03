package com.example.demo;

import com.ant.jt808.base.dto.jt808.basics.Message;
import com.example.demo.util.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class Resovler implements ApplicationRunner {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Value("${redis.key.queue.request.jt808}")
    String redisRequestKey;

    @Value("${redis.key.queue.response}")
    String redisResponseKey;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(
            new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true){
                            String data = stringRedisTemplate.opsForList().rightPop(redisRequestKey);
                            if (StringUtils.isEmpty(data)) {
                                // 暂无数据，等待3秒
                                System.out.println("%%% no redis data.sleep 3 seconds...");
                                Thread.sleep(3000);
                            }

                            Message msg = JsonUtils.toObj(Message.class, data);
                            // TODO
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        ).start();
    }
}
