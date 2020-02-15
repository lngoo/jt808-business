package com.example.demo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ant.msger.base.dto.persistence.PersistenceObject;
import com.ant.msger.base.dto.persistence.TopicUser;
import com.ant.msger.base.enums.OperateType;
import com.ant.msger.base.enums.SubjectType;
import com.ant.msger.base.message.MsgerTaskMsg;
import com.google.gson.JsonArray;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

    @Value("${redis.key.queue.response}")
    private String redisKeyResponse;

    @Value("${redis.key.queue.task}")
    private String redisKeyTask;

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        System.out.println(redisKeyResponse);
    }

    @Test
    void json() {
        String json = "[{\"expireTime\":\"2020-02-15 21:46:33\",\"topicId\":\"qunliao\",\"userId\":\"user3\"},{\"expireTime\":\"2020-02-15 21:51:33\",\"topicId\":\"qunliao\",\"userId\":\"user4\"}]";
        List<TopicUser> list0= JSONArray.parseArray(json, TopicUser.class); //OK
        List<TopicUser> list= JSONArray.parseArray(json).toJavaList(TopicUser.class); // failed
        System.out.println("ok!");
    }

    /**
     * 发送任务:add
     *
     * @throws Exception
     */
    @Test
    void sendTask() throws Exception {
        List<PersistenceObject> list = new ArrayList<>();
        list.add(new TopicUser("qunliao", "user1", null));
        list.add(new TopicUser("qunliao", "user2", null));
        list.add(new TopicUser("qunliao", "user3", new Date()));
        list.add(new TopicUser("qunliao", "user4", DateUtils.addMinutes(new Date(), 5)));
        MsgerTaskMsg msg = new MsgerTaskMsg(OperateType.ADD, SubjectType.TOPIC_USER, list);

        String json = JSONObject.toJSONStringWithDateFormat(msg, "yyyy-MM-dd HH:mm:ss");
        System.out.println(json);

        byte[] bytes = json.getBytes("UTF-8");

        redisTemplate.setValueSerializer(null);
        redisTemplate.setEnableDefaultSerializer(false);
        redisTemplate.convertAndSend(redisKeyTask, bytes);
        System.out.println("finished..");
    }


}
